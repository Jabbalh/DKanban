package kanban.service;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import kanban.bus.constants.EventBusNames;
import kanban.entity.db.Ticket;
import kanban.entity.db.User;
import kanban.entity.db.ZoneTicket;
import kanban.entity.db.parameter.ApplicationData;
import kanban.entity.ui.CardTicket;
import kanban.service.contract.IMongoService;
import kanban.service.utils.DbUtils;
import kanban.service.utils.UiUtils;

public class VerticleTicketService extends AbstractVerticle {

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
		
	}
	
	/**
	 * Mise à jour de l'état du ticket
	 * @param message
	 */
	private void updateTicketState(Message<String> message){
		// On récupère les paramètres
		JsonObject parameter = new JsonObject(message.body());		
		String stateName= parameter.getString("zone");
		ZoneTicket zone = new ZoneTicket(stateName);
		// On met à jour en BDD l'état (_ID -> ID du ticket | stateTicket -> état du ticket)
		mongoService.update(DbUtils.index(Ticket.class),
				new JsonObject().put("_id", parameter.getString("cardId")),  
				new JsonObject().put("$set", new JsonObject().put("zoneTicket", new JsonObject(Json.encodePrettily(zone)))), 
				r -> {
					// On publi une réponse avec le ticket, l'utilisateur concerné et le nouvel état
					JsonObject result = new JsonObject()
							.put("ticketId", parameter.getString("cardId"))
							.put("user", parameter.getString("userLogin"))
							.put("zone", stateName);
					vertx.eventBus().publish("update-card", Json.encodePrettily(result));
					message.reply("OK");
				});		
	}
	
	private void saveTicket(Message<JsonObject> message, boolean insert) {		
		JsonObject data = message.body();
		CardTicket card = Json.decodeValue(data.getJsonObject("ticket").encodePrettily(), CardTicket.class);
		
		String login = card.getOwner();
		vertx.eventBus().send(EventBusNames.USER_FIND_BY_LOGIN, login, user -> {
			Ticket ticket = new Ticket();
			ticket.setReference(card.getRef());
			ticket.set_id(card.getRef());
			ticket.setApplication(ApplicationData.get().getApplications().stream().filter(x -> x.getName().equals(card.getAppli())).findFirst().get());
			ticket.setCaisse(card.getCaisse());
			ticket.setDescription(card.getDescription());
			ticket.setSummary(card.getSummary());
			ticket.setZoneTicket(UiUtils.getZoneApp(data.getString("zone")).getZoneTicket());				
			ticket.setOwner(Json.decodeValue(user.result().body().toString(),User.class));
			
			if (insert){
				mongoService.insert(ticket, x -> {
					if (x.succeeded()) {		
						System.out.println("insertTicket -> OK");
						message.reply("OK");
					} else {
						System.out.println("insertTicket -> NOK");
						message.reply("NOK");
					}
					
				});
			} else {
				mongoService.update(ticket, x -> {
					message.reply((x) ? "OK" : "NOK");
				});
			}
			
		});						
	}
	
	
	
	/**
	 * Bus renvoyant la liste des tickets par utilisateur
	 * @param message
	 */
	private void handleTicketByUser(Message<String> message) {
		JsonObject request = new JsonObject();		
		request.put("owner.login", message.body());
		request.put("sort", "statesticket");
		
		mongoService.findAll(Ticket.class, request, x -> message.reply(Json.encodePrettily(x)));
	}
	
	
	
}
