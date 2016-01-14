package fr.kanban.front.application;

import fr.kanban.front.AbstractHandler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import kanban.bus.constants.EventBusNames;

public class ApplicationHandler extends AbstractHandler {

	public ApplicationHandler() {
		super();
	}
	
	public void apiGet(RoutingContext context, String eventBusName){
		vertx.eventBus().send(eventBusName,"ALL", x -> {
			context.response().end(x.result().body().toString());
		});
	}
	
	public void apiSet(RoutingContext context, String eventBusName, JsonObject data){
		vertx.eventBus().send(eventBusName, data, x-> context.response().end(x.result().body().toString()));
	}
	
	public void apiDelete(RoutingContext context,String className, JsonObject data){
		
		JsonObject send = new JsonObject()
								.put("clazz", className)
								.put("id", data.getString("_id"));
				
		vertx.eventBus().send(EventBusNames.ADMIN_DELETE, send, x-> context.response().end(x.result().body().toString()));
	}
	
	public void apiSet(RoutingContext context, String eventBusName){
		apiSet(context,eventBusName, context.getBodyAsJson());
	}

	/**
	 * Mise à jour du mot de passe
	 * @param context
     */
	public void apiUserUpdatePassword(RoutingContext context){
		// Structure :
		// data.login
		// data.oldPassword
		//data.newPassword
		JsonObject data = context.getBodyAsJson();
		vertx.eventBus().send(EventBusNames.ADMIN_USER_UP_PASSWORD, data, x -> context.response().end(x.result().body().toString()));

	}
	
	
}
