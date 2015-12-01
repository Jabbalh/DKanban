package kanban.service;

import java.util.Date;
import java.util.Optional;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import kanban.bus.constants.EventBusNames;
import kanban.entity.db.StateTicket;
import kanban.entity.db.Ticket;
import kanban.entity.db.TicketHistory;
import kanban.entity.db.User;
import kanban.entity.db.ZoneTicket;
import kanban.entity.db.parameter.ApplicationData;
import kanban.entity.ui.CardTicket;
import kanban.entity.ui.FullCard;
import kanban.service.contract.IMongoService;
import kanban.service.utils.DbUtils;
import kanban.service.utils.UiUtils;

public class VerticleTicketService extends AbstractVerticle {

	
	//private static final Logger logger = LoggerFactory.getLogger(VerticleTicketService.class);
	
	@Inject
	private IMongoService mongoService;
	
	@Override
	public void start() {
		
		vertx.eventBus().consumer(EventBusNames.KANBAN_TICKET_BY_USER, (Message<String> m) -> handleTicketByUser(m));		
		vertx.eventBus().consumer(EventBusNames.TICKET_LIST, x -> {
			mongoService.findAll(Ticket.class, new JsonObject(), r -> {
				x.reply(Json.encodePrettily(r));
			});
		});
		
		vertx.eventBus().consumer(EventBusNames.TICKET_UPDATE_STATE, 	(Message<String> m) -> updateTicketState(m));
		
		vertx.eventBus().consumer(EventBusNames.TICKET_INSERT_ALL, 	(Message<JsonObject> m) -> saveTicket(m,true));
		vertx.eventBus().consumer(EventBusNames.TICKET_UPDATE_ALL, 	(Message<JsonObject> m) -> saveTicket(m, false));
		vertx.eventBus().consumer(EventBusNames.TICKET_ARCHIVE, 	(Message<JsonObject> m) -> archiveTicket(m));
	}
	
	/**
	 * Archivage d'un ticket
	 * @param message
	 */
	private void archiveTicket(Message<JsonObject> message){
		CardTicket card = Json.decodeValue(message.body().encodePrettily(), CardTicket.class);
		
		mongoService.update(DbUtils.index(Ticket.class), new JsonObject().put("_id", card.getRef()), 
				new JsonObject().put("$set", new JsonObject().put("archive", true)), x -> {
					JsonObject result = new JsonObject()
							.put("card", message.body())
							.put("user", card.getOwner())
							.put("zone", "NA");
					vertx.eventBus().publish("update-card", Json.encodePrettily(result));
					message.reply(Json.encodePrettily(x));
				});
	}
	
	/**
	 * Mise à jour de l'état du ticket
	 * @param message
	 */
	private void updateTicketState(Message<String> message){
		// On récupère les paramètres
		JsonObject parameter = new JsonObject(message.body());		
		String stateName= parameter.getString("zone");		
		
		
		mongoService.findOne(Ticket.class, new JsonObject().put("_id", parameter.getJsonObject("card").getString("id")))
		.when(ticket -> {
			
			ticket.addHistory(new TicketHistory(t -> {
				String desc = String.format("Changement de Zone:  %s de %s vers %s de %s",
										ticket.getZoneTicket().getCodeZone(),ticket.getOwner().getLogin(),
										parameter.getString("zone"),parameter.getString("user"));				
				t.setDate(new Date());
				t.setSummary("Mise à jour de la zone");
				t.setDescription(desc);
			}));
			
			mongoService.findOne(ZoneTicket.class, new JsonObject().put("codeZone", parameter.getString("zone")))
			.when(zone -> 
			{
				mongoService.findOne(User.class, new JsonObject().put("login", parameter.getString("user")))
				.when(user -> 
				{
					ticket.setOwner(user);
					ticket.setZoneTicket(zone);
					mongoService.update(ticket, r -> {	
								FullCard fullCard = new FullCard();
								fullCard.setCard(CardTicket.fromTicket(ticket));
								fullCard.setUser(parameter.getString("user"));
								fullCard.setZone(stateName);
								// On publi une réponse avec le ticket, l'utilisateur concerné et le nouvel état								
								message.reply(Json.encodePrettily("OK"));
								vertx.eventBus().publish("update-card", Json.encodePrettily(fullCard));								
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
		mongoService.findInternListFromObject(Ticket.class,TicketHistory.class, new JsonObject().put("_id", ticket.get_id()), new JsonObject().put("ticketHistory", "1"))
		.when(r -> {
			ticket.setTicketHistory(r);
			ticket.addHistory(new TicketHistory(h -> {
				h.setDate(new Date());
						h.setSummary("Mise à jour");
						h.setDescription("Commentaire mis par le système");
			}));
			
			mongoService.update(ticket, x -> {
				if (x){
					message.reply("OK");
					fullCard.setCard(CardTicket.fromTicket(ticket));				
					vertx.eventBus().publish("update-card", Json.encodePrettily(fullCard));						
					
				} else {
					message.reply("NOK");
				}					
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
		mongoService.getNextSequence(Ticket.class).when(id -> {			
			ticket.set_id(id.toString());
			ticket.addHistory(new TicketHistory(h -> {
					h.setDate(new Date());
					h.setSummary("Insertion");
					h.setDescription("Commentaire mis par le système");
			}));
			mongoService.insert(ticket, x -> {
				if (x.succeeded()) {									
					message.reply("OK");	
					fullCard.setCard(CardTicket.fromTicket(ticket));					
					vertx.eventBus().publish("insert-card", Json.encodePrettily(fullCard));							
				} else {
					
					message.reply("NOK");
				}
				
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
		mongoService.findAll(Ticket.class, request, x -> message.reply(Json.encodePrettily(x)));
	}
	
	
	
}
