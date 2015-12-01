package fr.kanban.front.application;

import fr.kanban.front.AbstractHandler;
import io.vertx.ext.web.RoutingContext;
import kanban.bus.constants.EventBusNames;

public class ApplicationHandler extends AbstractHandler {

	public ApplicationHandler() {
		super();
	}
	
	public void apiApplicationList(RoutingContext context){
		vertx.eventBus().send(EventBusNames.APPLICATION_LIST, "ALL", x -> {
			context.response().end(x.result().body().toString());
		});
				
	}
	
	public void apiStateList(RoutingContext context){
		vertx.eventBus().send(EventBusNames.STATE_LIST,"ALL", x -> {
			context.response().end(x.result().body().toString());
		});
	}
}
