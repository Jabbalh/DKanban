package fr.kanban.front.ticket;

import fr.kanban.front.AbstractHandler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import kanban.bus.constants.EventBusNames;

public class TicketHandler extends AbstractHandler {

	public TicketHandler() {
		super();
	}

	public void apiTicketByUser(RoutingContext context) {
		String l = context.request().getParam("login");			
		vertx.eventBus().send(EventBusNames.KANBAN_TICKET_BY_USER, l, r -> {				
			context.response().end(r.result().body().toString());
		});
	}
	
	public void apiTicketUpdate(RoutingContext context){			
		JsonObject data = context.getBodyAsJson();			
		System.out.println("update ticket -> " + data.encodePrettily());
		vertx.eventBus().send(EventBusNames.TICKET_UPDATE_STATE, Json.encodePrettily(data), r -> {				
			context.response().end(Json.encodePrettily("UPDATE OK)"));
			
		});		
		
	}
	
	public void apiTicketList(RoutingContext context){
		vertx.eventBus().send(EventBusNames.TICKET_LIST, "LISTE", r -> {
			context.response().end(r.result().body().toString());
		});
	}

}
