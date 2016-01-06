package fr.kanban.front.user;

import fr.kanban.front.AbstractHandler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import kanban.bus.constants.EventBusNames;
import kanban.db.entity.User;

public class UserHandler extends AbstractHandler {
	
	//private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);
	
	public UserHandler() { super();	}
	
	public void apiUserList(RoutingContext context){
		vertx.eventBus().send(EventBusNames.USER_LIST, "", r -> {			
			context.response().end(r.result().body().toString());
		});
	}
	
	public void apiUserByLogin(RoutingContext context){
		vertx.eventBus().send(EventBusNames.USER_FIND_BY_LOGIN, context.request().getParam("login"), x-> {			
			context.response().end(Json.encodePrettily(x.result().body().toString()));			
		});
	}
	
	public void userSave(RoutingContext context){
		vertx.eventBus().send(EventBusNames.USER_SAVE, context.getBodyAsJson().getJsonObject("data"), x-> {
			context.response().end(Json.encodePrettily(x.result().body().toString()));
		});
	}
	
	public void apiUserInsert(RoutingContext context){
		JsonObject user = context.getBodyAsJson();
		vertx.eventBus().send(EventBusNames.USER_INSERT, user, x -> {
			context.response().end(x.result().body().toString());
		});
	}
	
	public void apiUserNew(RoutingContext context){
		User user = new User();
		context.response().end(Json.encodePrettily(user));
	}
	
	
	
}
