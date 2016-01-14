package fr.kanban.front.kanban;

import fr.kanban.front.AbstractHandler;
import io.vertx.ext.web.RoutingContext;
import kanban.bus.constants.EventBusNames;

public class KanbanHandler extends AbstractHandler {

	public KanbanHandler() { super();	}

	public void apiKanbanHeaders(RoutingContext context){
		vertx.eventBus().send(EventBusNames.KANBAN_HEADER_LIST, "",
				r -> {
					context.response().end(r.result().body().toString());
				});
	}

	public void apiKanbanHeadersPriority(RoutingContext context){
		vertx.eventBus().send(EventBusNames.KANBAN_HEADER_LIST_PRIORITY, "",
				r -> {
					context.response().end(r.result().body().toString());
				});
	}
	
	public void apiKanbanByUser(RoutingContext context){		
		vertx.eventBus().send(EventBusNames.KANBAN_BY_USER, context.request().getParam("user"),
				r -> context.response().end(r.result().body().toString()));
	}
	public void apiKanbanByPriority(RoutingContext context){
		vertx.eventBus().send(EventBusNames.KANBAN_BY_USER_FOR_PRIORITY, context.request().getParam("user"),
				r -> context.response().end(r.result().body().toString()));
	}

	
}
