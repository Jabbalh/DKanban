package kanban.service;

import java.util.Date;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import kanban.bus.constants.EventBusNames;
import kanban.entity.db.Ticket;
import kanban.entity.db.TicketHistory;
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
		vertx.eventBus().consumer(EventBusNames.TICKET_ARCHIVE, 	(Message<JsonObject> m) -> archiveTicket(m));
	}
	
	
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
		
		mongoService.findOne(ZoneTicket.class, new JsonObject().put("codeZone", parameter.getString("zone")), zone -> {
			mongoService.findOne(User.class, new JsonObject().put("login", parameter.getString("user")), user -> {
				mongoService.update(DbUtils.index(Ticket.class),
						new JsonObject().put("_id", parameter.getJsonObject("card").getString("id")),  
						new JsonObject().put("$set", new JsonObject()
								.put("zoneTicket", new JsonObject(Json.encodePrettily(zone)))
								.put("owner", new JsonObject(Json.encodePrettily(user)))), 
						r -> {
							System.out.println("updateTicketState -> " + parameter.encodePrettily());
							// On publi une réponse avec le ticket, l'utilisateur concerné et le nouvel état
							JsonObject result = new JsonObject()
									.put("card",new JsonObject().put("id", parameter.getJsonObject("card").getString("id")))
									.put("user", parameter.getString("user"))
									.put("zone", stateName);
												
							message.reply(Json.encodePrettily("OK"));
							vertx.eventBus().publish("update-card", Json.encodePrettily(result));
						});		
			});
		});
		
	}
	
	private void saveTicket(Message<JsonObject> message, final boolean insert) {		
		JsonObject data = message.body();
		JsonObject cardJson = data.getJsonObject("ticket");
		cardJson.put("id", cardJson.getString("ref"));
		CardTicket card = Json.decodeValue(cardJson.encodePrettily(), CardTicket.class);
		
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
			ticket.addHistory(new TicketHistory(x -> {
											x.setDate(new Date());
											x.setSummary((insert)?"Insertion" : "Mise à jour");
										}));
			
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
			JsonObject sendBus = new JsonObject();
			//result.user+'$'+result.zone
			//result.card
			sendBus.put("card", cardJson)
					.put("user", login)
					.put("zone", data.getString("zone"));
			vertx.eventBus().publish(((insert)?"insert-card":"update-card"), sendBus.encodePrettily());
			
		});						
	}
	
	
	
	/**
	 * Bus renvoyant la liste des tickets par utilisateur
	 * @param message
	 */
	private void handleTicketByUser(Message<String> message) {
		JsonObject request = new JsonObject("{'owner.login':'"+ message.body()+"','archive:false'");		
		//request.put("owner.login", message.body());
		//request.put("archive", false);
		request.put("sort", "statesticket");
		System.out.println("handleTicketByUser -> " + request.encodePrettily());
		mongoService.findAll(Ticket.class, request, x -> message.reply(Json.encodePrettily(x)));
	}
	
	
	
}
