package fr.kanban.front.ticket;

import java.util.function.Consumer;

import fr.kanban.front.AbstractHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import kanban.bus.constants.EventBusNames;
import kanban.db.entity.Ticket;
import kanban.web.services.ISessionService;

public class TicketHandler extends AbstractHandler {

	//private static final Logger logger = LoggerFactory.getLogger(TicketHandler.class);
	
	
	private ISessionService sessionService;
	
	public TicketHandler(ISessionService sessionService) {
		super();
		this.sessionService = sessionService;		
	}
	
	

	public void apiTicketByUser(RoutingContext context) {
		String l = context.request().getParam("login");			
		vertx.eventBus().send(EventBusNames.KANBAN_TICKET_BY_USER, l, r -> {				
			context.response().end(r.result().body().toString());
		});
	}
	
	public void apiTicketUpdateZone(RoutingContext context){		
		ticketUpdateProperty(context,EventBusNames.TICKET_UPDATE_STATE);
	}

	public void apiTicketUpdatePriority(RoutingContext context){
		ticketUpdateProperty(context,EventBusNames.TICKET_UPDATE_PRIORITY);
	}

	private void ticketUpdateProperty(RoutingContext context, String eventBus){
		JsonObject data = context.getBodyAsJson().getJsonObject("data");
		vertx.eventBus().send(eventBus, data, r -> {
			context.response().end(Json.encodePrettily("OK"));
		});
	}

	
	
	public void apiTicketUpdateAll(RoutingContext context) {		
		JsonObject data = context.getBodyAsJson();		
		Consumer<Message<Object>> callback = x -> context.response().end(Json.encodePrettily(x.body().toString())); 
		if (data.getBoolean("insert")) {
			vertx.eventBus().send(EventBusNames.TICKET_INSERT_ALL, data.getJsonObject("card"), x -> callback.accept(x.result()));
		} else {
			vertx.eventBus().send(EventBusNames.TICKET_UPDATE_ALL, data.getJsonObject("card"), x -> callback.accept(x.result()));
		}
	}
	
	
	public void apiTicketList(RoutingContext context){
		vertx.eventBus().send(EventBusNames.TICKET_LIST, "LISTE", r -> {
			context.response().end(r.result().body().toString());
		});
	}
	
	public void apiTicketSearch(RoutingContext context) {
		vertx.eventBus().send(EventBusNames.TICKET_SEARCH, context.getBodyAsJson(), r -> {
			context.response().end(r.result().body().toString());
		});
	}
	
	public void apiTicketDelete(RoutingContext context){
		vertx.eventBus().send(EventBusNames.TICKET_DELETE, context.getBodyAsJson(), r -> context.response().end(r.result().body().toString()));
	}
	
	public void apiNewEmpty(RoutingContext context) {
		context.response().end(
				Json.encodePrettily(new Ticket(sessionService.getCurrentUser(context.session())))
				); 
	}
	
	public void apiArchive(RoutingContext context) {
		JsonObject data = context.getBodyAsJson();
		vertx.eventBus().send(EventBusNames.TICKET_ARCHIVE, data,x -> {
			context.response().end(x.result().body().toString());
		});
	}

}
