package fr.kanban.front.user;

import fr.kanban.front.AbstractHandler;
import io.vertx.ext.web.RoutingContext;
import kanban.bus.constants.EventBusNames;

public class UserHandler extends AbstractHandler {
	
	public UserHandler() { super();	}
	
	public void apiUserList(RoutingContext context){
		vertx.eventBus().send(EventBusNames.USER_LIST, "", r -> {
			System.out.println("UserList -> " + r.result().body().toString());
			context.response().end(r.result().body().toString());
		});
	}
	
}
