package fr.kanban.front.user;

import fr.kanban.front.AbstractHandler;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import kanban.bus.constants.EventBusNames;

public class UserHandler extends AbstractHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);
	
	public UserHandler() { super();	}
	
	public void apiUserList(RoutingContext context){
		vertx.eventBus().send(EventBusNames.USER_LIST, "", r -> {
			logger.debug("apiUserList -> " + r.result().body().toString());
			context.response().end(r.result().body().toString());
		});
	}
	
	public void apiUserByLogin(RoutingContext context){
		vertx.eventBus().send(EventBusNames.USER_FIND_BY_LOGIN, context.request().getParam("login"), x-> {
			logger.debug("apiUserByLogin -> " + x.result().body().toString());
			context.response().end(Json.encodePrettily(x.result().body().toString()));			
		});
	}
	
}
