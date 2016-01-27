package kanban.service;

import java.util.Date;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import kanban.bus.constants.EventBusNames;
import kanban.db.entity.*;
import kanban.entity.session.ApplicationData;
import kanban.service.contract.IMongoService;
import kanban.service.utils.DbUtils;
import kanban.ui.entity.SearchQuery;
import kanban.utils.callback.Async;

public class VerticleTicketService extends AbstractVerticle {

	
	private static final Logger logger = LoggerFactory.getLogger(VerticleTicketService.class);
	
	@Inject
	private IMongoService mongoService;
	
	@Override
	public void start() {
		
		vertx.eventBus().consumer(EventBusNames.KANBAN_TICKET_BY_USER, this::handleTicketByUser);
		vertx.eventBus().consumer(EventBusNames.TICKET_LIST, x ->
				Async.When(() -> mongoService.findAll(Ticket.class, new JsonObject())).doThat(r -> x.reply(Json.encodePrettily(r))));
		
		vertx.eventBus().consumer(EventBusNames.TICKET_UPDATE_STATE, 	this::updateTicketState);
		vertx.eventBus().consumer(EventBusNames.TICKET_UPDATE_PRIORITY,	this::updateTicketPriority);
		
		vertx.eventBus().consumer(EventBusNames.TICKET_INSERT_ALL, 	this::insert);
		vertx.eventBus().consumer(EventBusNames.TICKET_UPDATE_ALL, 	this::update);
		vertx.eventBus().consumer(EventBusNames.TICKET_ARCHIVE, 	this::archiveTicket);
		
		vertx.eventBus().consumer(EventBusNames.TICKET_SEARCH, this::searchTicket);
		
		vertx.eventBus().consumer(EventBusNames.TICKET_DELETE, this::deleteTicket);
		
	}
	
	
	
	private void searchTicket(Message<JsonObject> m) {
		
		SearchQuery searchQuery = Json.decodeValue(m.body().getJsonObject("data").encodePrettily(), SearchQuery.class);
		
		JsonObject query = new JsonObject();
		
		String textQuery = "";
		if (!searchQuery.getReference().equals("")) textQuery = searchQuery.getReference() + " ";
		if (!searchQuery.getDescription().equals("")) textQuery += searchQuery.getDescription();
		
		if (!textQuery.equals("")) query.put("$text", new JsonObject().put("$search",textQuery));				
		
		if (searchQuery.getApplication() != null) 		{ query.put("application",new JsonObject(Json.encode(searchQuery.getApplication()))); }
		if (searchQuery.getOwner() != null)				{ query.put("owner", new JsonObject(Json.encode(searchQuery.getOwner()))); }
		if (searchQuery.getArchive() != null)			{ query.put("archive", searchQuery.getArchive()); }
				
		Async.When(()->mongoService.findAll(Ticket.class,query)).doThat(x -> m.reply(Json.encodePrettily(x)) );
		
		
	}

	/**
	 * Archivage d'un ticket
	 * @param message
	 */
	private void archiveTicket(Message<JsonObject> message){
		Ticket card = Json.decodeValue(message.body().encodePrettily(), Ticket.class);
		
		Async.When(() -> mongoService.update(DbUtils.index(Ticket.class), new JsonObject().put("_id", card.get_id()), 
				new JsonObject().put("$set", new JsonObject().put("archive", true))))
		.doThat( x -> {					
					vertx.eventBus().publish(EventBusNames.DELETE_CARD, Json.encodePrettily(message.body()));
					message.reply(Json.encodePrettily(x));
				});
	}
	
	private void deleteTicket(Message<JsonObject> message){
		Async.When(()->mongoService.deleteEntity(Ticket.class,message.body().getString("_id")))
		.Rule(r -> r)
		.Otherwise(c -> message.reply(Json.encodePrettily("KO")))
		.doThat(r -> {				
			vertx.eventBus().publish(EventBusNames.DELETE_CARD, Json.encodePrettily(message.body()));
			message.reply(Json.encodePrettily(r));
		});
	}
	/*
	private void replyKo(Message<String> message){
		message.reply(Json.encodePrettily("KO"));
	}*/
	
	/**
	 * Mise à jour de l'état du ticket
	 * @param message
	 */
	private void updateTicketState(Message<JsonObject> message){
		// On récupère les paramètres
		String zone = message.body().getString("zone");
		Ticket ticket = Json.decodeValue(message.body().getJsonObject("card").encode(), Ticket.class);
		String login = message.body().getString("user");
		
		
		ZoneParameter zoneParameter = ApplicationData.get().getZones().stream().filter(x -> x.getCode().equals(zone)).findFirst().get();
		
		
		Async.When(()-> mongoService.findOne(User.class, new JsonObject().put("login", login)))
			.doThat(user -> {
				ticket.addHistory(new TicketHistory(t -> {
					String desc = String.format("Changement de Zone:  %s de %s vers %s de %s",ticket.getZone().getCode(),ticket.getOwner().getCode(),zone,login);				
					t.setDateCreation(new Date());
					t.setSummary("Mise à jour de la zone");
					t.setDescription(desc);
				}));
				
				ticket.setOwner(ParamTuple.from(user));
				ticket.setZone(ParamTuple.from(zoneParameter));
				Async.When(()-> mongoService.update(ticket) )
				.Rule(r -> r)
				.Otherwise(x -> message.reply(Json.encode(x)))
				.doThat(r -> {
					message.reply(Json.encodePrettily("OK"));
					logger.debug("updateTicketState -> vertx.eventBus().publish(\"update-card\"");
					vertx.eventBus().publish(EventBusNames.UPDATE_CARD, Json.encodePrettily(ticket));
				});
				
			});		
	}

	/**
	 * Mise à jour de l'état du ticket
	 * @param message
	 */
	private void updateTicketPriority(Message<JsonObject> message){
		// On récupère les paramètres
		String priority = message.body().getString("zone");
		Ticket ticket = Json.decodeValue(message.body().getJsonObject("card").encode(), Ticket.class);
		String login = message.body().getString("user");


		PriorityParameter priorityParameter = ApplicationData.get().getPriority().stream().filter(x -> x.getCode().equals(priority)).findFirst().get();


		Async.When(()-> mongoService.findOne(User.class, new JsonObject().put("login", login)))
				.doThat(user -> {
					ticket.addHistory(new TicketHistory(t -> {
						String desc = String.format("Changement de priorité:  %s de %s vers %s de %s",ticket.getPriority().getCode(),ticket.getOwner().getCode(),priority,login);
						t.setDateCreation(new Date());
						t.setSummary("Mise à jour de la zone");
						t.setDescription(desc);
					}));

					ticket.setOwner(ParamTuple.from(user));
					ticket.setPriority(ParamTuple.from(priorityParameter));
					Async.When(()-> mongoService.update(ticket) )
							.Rule(r -> r)
							.Otherwise(x -> message.reply(Json.encode(x)))
							.doThat(r -> {
								message.reply(Json.encodePrettily("OK"));
								logger.debug("updateTicketState -> vertx.eventBus().publish(\"update-card\"");
								vertx.eventBus().publish(EventBusNames.UPDATE_CARD, Json.encodePrettily(ticket));
							});

				});
	}


	/**
	 * Mise à jour d'un ticket
	 * @param message
     */
	private void update(Message<JsonObject> message) {
		Ticket ticket = Json.decodeValue(message.body().encode(), Ticket.class);
		Async.When(() -> mongoService.update(ticket))
		.Rule(rule -> rule)
		.Otherwise(other -> message.reply("NOK"))
		.doThat(x -> {				
			message.reply("OK");		
			logger.info("update -> " + EventBusNames.UPDATE_CARD);
			vertx.eventBus().publish(EventBusNames.UPDATE_CARD, Json.encodePrettily(ticket));						
		});		
	}

	/**
	 * Insertion d'un ticket
	 * @param message
     */
	private void insert(Message<JsonObject> message){
		Ticket ticket = Json.decodeValue(message.body().encode(), Ticket.class);
		Async.When(()->mongoService.getNextSequence(Ticket.class)).doThat(id -> {			
			ticket.set_id(id.toString());
			ticket.addHistory(new TicketHistory(h -> {
					h.setDateCreation(new Date());
					h.setSummary("Création du ticket");
					h.setDescription("Commentaire mis par le système");
			}));
			Async.When(() -> mongoService.insert(ticket))
			.Rule(rule -> rule)
			.Otherwise(other -> message.reply("NOK"))
			.doThat(x -> {										
				message.reply("OK");								
				vertx.eventBus().publish(EventBusNames.INSERT_CARD, Json.encodePrettily(ticket));
				logger.info("update -> " + EventBusNames.INSERT_CARD);
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
