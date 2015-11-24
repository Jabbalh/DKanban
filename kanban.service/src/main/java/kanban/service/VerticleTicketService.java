package kanban.service;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import kanban.bus.constants.EventBusNames;
import kanban.entity.db.StateTicket;
import kanban.entity.db.Ticket;
import kanban.service.contract.IMongoService;
import kanban.service.utils.DbUtils;

public class VerticleTicketService extends AbstractVerticle {

	@Inject
	private IMongoService mongoService;
	
	@Override
	public void start() {
		
		vertx.eventBus().consumer(EventBusNames.KANBAN_TICKET_BY_USER, (Message<String> m) -> handleTicketByUser(m));		
		vertx.eventBus().consumer(EventBusNames.TICKET_LIST, x -> {
			mongoService.findAll(DbUtils.index(Ticket.class), Ticket.class, new JsonObject(), r -> {
				x.reply(Json.encodePrettily(r));
			});
		});
		
		vertx.eventBus().consumer(EventBusNames.TICKET_UPDATE_STATE, 	(Message<String> m) -> updateTicketState(m));
		
	}
	
	/**
	 * Mise à jour de l'état du ticket
	 * @param message
	 */
	private void updateTicketState(Message<String> message){
		// On récupère les paramètres
		JsonObject parameter = new JsonObject(message.body());		
		String stateName= parameter.getString("state");
		StateTicket state = new StateTicket(stateName);
		// On met à jour en BDD l'état (_ID -> ID du ticket | stateTicket -> état du ticket)
		mongoService.update(DbUtils.index(Ticket.class),
				new JsonObject().put("_id", parameter.getString("cardId")),  
				new JsonObject().put("$set", new JsonObject().put("stateTicket", new JsonObject(Json.encodePrettily(state)))), 
				r -> {
					// On publi une réponse avec le ticket, l'utilisateur concerné et le nouvel état
					JsonObject result = new JsonObject()
							.put("ticketId", parameter.getString("cardId"))
							.put("user", parameter.getString("userLogin"))
							.put("state", stateName);
					vertx.eventBus().publish("update-card", Json.encodePrettily(result));
					message.reply("OK");
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
		
		mongoService.findAll(DbUtils.index(Ticket.class), Ticket.class, request, x -> message.reply(Json.encodePrettily(x)));
	}
	
	
	
}
