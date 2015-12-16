package kanban.service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import kanban.bus.constants.EventBusNames;
import kanban.entity.db.StateTicket;
import kanban.entity.db.Ticket;
import kanban.entity.db.TicketHistory;
import kanban.entity.db.User;
import kanban.entity.db.ZoneTicket;
import kanban.entity.db.parameter.ApplicationData;
import kanban.entity.ui.CardTicket;
import kanban.entity.ui.FullCard;
import kanban.entity.ui.SearchQuery;
import kanban.service.contract.IMongoService;
import kanban.service.utils.DbUtils;
import kanban.service.utils.UiUtils;
import kanban.utils.callback.Async;

public class VerticleTicketService extends AbstractVerticle {

	
	private static final Logger logger = LoggerFactory.getLogger(VerticleTicketService.class);
	
	@Inject
	private IMongoService mongoService;
	
	@Override
	public void start() {
		
		vertx.eventBus().consumer(EventBusNames.KANBAN_TICKET_BY_USER, (Message<String> m) -> handleTicketByUser(m));		
		vertx.eventBus().consumer(EventBusNames.TICKET_LIST, x -> 
			{
				Async.When(() -> mongoService.findAll(Ticket.class, new JsonObject())).doThat(r -> x.reply(Json.encodePrettily(r)));			
			});
		
		vertx.eventBus().consumer(EventBusNames.TICKET_UPDATE_STATE, 	(Message<String> m) -> updateTicketState(m));
		
		vertx.eventBus().consumer(EventBusNames.TICKET_INSERT_ALL, 	(Message<JsonObject> m) -> saveTicket(m,true));
		vertx.eventBus().consumer(EventBusNames.TICKET_UPDATE_ALL, 	(Message<JsonObject> m) -> saveTicket(m, false));
		vertx.eventBus().consumer(EventBusNames.TICKET_ARCHIVE, 	(Message<JsonObject> m) -> archiveTicket(m));
		
		vertx.eventBus().consumer(EventBusNames.TICKET_SEARCH, (Message<JsonObject> m) -> searchTicket(m));
		
		vertx.eventBus().consumer(EventBusNames.TICKET_DELETE, (Message<JsonObject> m) -> deleteTicket(m));
		
	}
	
	
	
	private void searchTicket(Message<JsonObject> m) {
		
		SearchQuery searchQuery = Json.decodeValue(m.body().getJsonObject("data").encodePrettily(), SearchQuery.class);
		
		JsonObject query = new JsonObject();
		
		String textQuery = "";
		if (!searchQuery.getReference().equals("")) textQuery = searchQuery.getReference() + " ";
		if (!searchQuery.getDescription().equals("")) textQuery += searchQuery.getDescription();
		
		if (!textQuery.equals("")) query.put("$text", new JsonObject().put("$search",textQuery));				
		
		if (!searchQuery.getApplication().equals("") || !searchQuery.getOwner().equals(""))
		{
			if (!searchQuery.getApplication().equals("")) query.put("application.name", searchQuery.getApplication());
			if (!searchQuery.getOwner().equals("")) query.put("owner.login", searchQuery.getOwner());															
		}
				
		System.out.println("searchTicket -> " + query.encodePrettily());
		
		
		Async.When(()->mongoService.findAll(Ticket.class,query))
		.doThat(x -> {
			List<CardTicket> result = x.stream().map(ticket -> CardTicket.fromTicket(ticket)).collect(Collectors.toList());			
			m.reply(Json.encodePrettily(result));
		});
		
		
	}

	/**
	 * Archivage d'un ticket
	 * @param message
	 */
	private void archiveTicket(Message<JsonObject> message){
		CardTicket card = Json.decodeValue(message.body().encodePrettily(), CardTicket.class);
		
		Async.When(() -> mongoService.update(DbUtils.index(Ticket.class), new JsonObject().put("_id", card.getId()), 
				new JsonObject().put("$set", new JsonObject().put("archive", true))))
		.doThat( x -> {
					JsonObject result = new JsonObject()
							.put("card", message.body())
							.put("user", card.getOwner())
							.put("zone", "NA");
					vertx.eventBus().publish(EventBusNames.DELETE_CARD, Json.encodePrettily(result));
					message.reply(Json.encodePrettily(x));
				});
	}
	
	private void deleteTicket(Message<JsonObject> message){
		Async.When(()->mongoService.deleteEntity(Ticket.class,message.body().getString("id")))
		.Rule(r -> r == true)
		.Otherwise(c -> message.reply(Json.encodePrettily("KO")))
		.doThat(r -> {
			CardTicket card = Json.decodeValue(message.body().encodePrettily(), CardTicket.class);
			JsonObject result = new JsonObject()
					.put("card", message.body())
					.put("user", card.getOwner())
					.put("zone", "NA");
			vertx.eventBus().publish(EventBusNames.DELETE_CARD, Json.encodePrettily(result));
			message.reply(Json.encodePrettily(r));
		});
	}
	
	private void replyKo(Message<String> message){
		message.reply(Json.encodePrettily("KO"));
	}
	
	/**
	 * Mise à jour de l'état du ticket
	 * @param message
	 */
	private void updateTicketState(Message<String> message){
		// On récupère les paramètres
		JsonObject parameter = new JsonObject(message.body());		
		String stateName= parameter.getString("zone");		
		
		Async.When(() -> mongoService.findOne(Ticket.class, new JsonObject().put("_id", parameter.getJsonObject("card").getString("id"))))
		.Rule(Objects::nonNull)
		.Otherwise(x -> replyKo(message))
		.doThat(ticket -> {
			System.out.println("updateTicketState -> FindOne");
			ticket.addHistory(new TicketHistory(t -> {
				String desc = String.format("Changement de Zone:  %s de %s vers %s de %s",
										ticket.getZoneTicket().getCodeZone(),ticket.getOwner().getLogin(),
										parameter.getString("zone"),parameter.getString("user"));				
				t.setDate(new Date());
				t.setSummary("Mise à jour de la zone");
				t.setDescription(desc);
			}));
			
			Async.When(() -> mongoService.findOne(ZoneTicket.class, new JsonObject().put("codeZone", parameter.getString("zone"))))
			.Rule(Objects::nonNull)
			.Otherwise(x -> replyKo(message))
			.doThat(zone -> 
			{
				Async.When(() -> mongoService.findOne(User.class, new JsonObject().put("login", parameter.getString("user"))))
				.Rule(Objects::nonNull)
				.Otherwise(x -> replyKo(message))
				.doThat(user -> 
				{
					ticket.setOwner(user);
					ticket.setZoneTicket(zone);
					Async.When(() -> mongoService.update(ticket))
							.doThat(r -> {	
								FullCard fullCard = new FullCard();
								fullCard.setCard(CardTicket.fromTicket(ticket));
								fullCard.setUser(parameter.getString("user"));
								fullCard.setZone(stateName);
								// On publie une réponse avec le ticket, l'utilisateur concerné et le nouvel état								
								message.reply(Json.encodePrettily("OK"));
								logger.debug("updateTicketState -> vertx.eventBus().publish(\"update-card\"");
								vertx.eventBus().publish(EventBusNames.UPDATE_CARD, Json.encodePrettily(fullCard));								
							});
				});
			});	
			
			
		});
				
	}
	
	/**
	 * Sauvegarde d'un ticket
	 * @param message
	 * @param insert
	 */
	private void saveTicket(Message<JsonObject> message, final boolean insert) {		
		JsonObject data = message.body();
		FullCard fullCard = Json.decodeValue(data.encodePrettily(), FullCard.class);
		
		vertx.eventBus().send(EventBusNames.USER_FIND_BY_LOGIN, fullCard.getCard().getOwner(), user -> {
			CardTicket card = fullCard.getCard();			
			Ticket ticket = new Ticket();						
			ticket.setReference(card.getRef());
			ticket.set_id(card.getId());
			ticket.setApplication(ApplicationData.get().getApplications().stream().filter(x -> x.getName().equals(card.getAppli())).findFirst().orElse(null));
			Optional<StateTicket> stateTicket = ApplicationData.get().getStatesTicket().stream().filter(x -> x.getCode().equals(card.getState())).findFirst();
			ticket.setStateTicket(stateTicket.orElse(null));
			ticket.setCaisse(card.getCaisse());
			ticket.setDescription(card.getDescription());
			ticket.setSummary(card.getSummary());
			ticket.setZoneTicket(UiUtils.getZoneApp(data.getString("zone")).getZoneTicket());				
			ticket.setOwner(Json.decodeValue(user.result().body().toString(),User.class));
			
			
			if (insert){				
				this.insert(message, ticket, fullCard);								
			} else {
				this.update(message, ticket, fullCard);
			}						
		});						
	}
	
	/**
	 * Mise à jour d'un ticket
	 * @param message
	 * @param ticket
	 * @param fullCard
	 */
	private void update(Message<JsonObject> message,Ticket ticket,FullCard fullCard) {
		Async.When(() -> mongoService.findInternListFromObject(
				Ticket.class,TicketHistory.class, new JsonObject().put("_id", ticket.get_id()), new JsonObject().put("ticketHistory", "1")))
		.doThat(r -> {
			//ticket.setTicketHistory(r);
			ticket.setTicketHistory(fullCard.getCard().toTicketHistory());
			Async.When(() -> mongoService.update(ticket))
			.Rule(rule -> rule)
			.Otherwise(other -> message.reply("NOK"))
			.doThat(x -> {				
				message.reply("OK");
				fullCard.setCard(CardTicket.fromTicket(ticket));				
				vertx.eventBus().publish(EventBusNames.UPDATE_CARD, Json.encodePrettily(fullCard));						
			});
		});
		
	}
	
	/**
	 * Insertion d'un ticket
	 * @param message
	 * @param ticket
	 * @param fullCard
	 */
	private void insert(Message<JsonObject> message,Ticket ticket, FullCard fullCard){
		Async.When(()->mongoService.getNextSequence(Ticket.class)).doThat(id -> {			
			ticket.set_id(id.toString());
			ticket.addHistory(new TicketHistory(h -> {
					h.setDate(new Date());
					h.setSummary("Création du ticket");
					h.setDescription("Commentaire mis par le système");
			}));
			Async.When(() -> mongoService.insert(ticket))
			.Rule(rule -> rule)
			.Otherwise(other -> message.reply("NOK"))
			.doThat(x -> {										
				message.reply("OK");	
				fullCard.setCard(CardTicket.fromTicket(ticket));					
				vertx.eventBus().publish(EventBusNames.INSERT_CARD, Json.encodePrettily(fullCard));
			});
		});
	}
	
	
	
	
	
	/**
	 * Bus renvoyant la liste des tickets par utilisateur
	 * @param message
	 */
	@Deprecated
	private void handleTicketByUser(Message<String> message) {
		JsonObject request = new JsonObject("{'owner.login':'"+ message.body()+"','archive:false'");		
		request.put("sort", "statesticket");		
		Async.When(() -> mongoService.findAll(Ticket.class, request)).doThat(x -> message.reply(Json.encodePrettily(x)));
	}
	
	
	
}
