package fr.kanban.front.auth;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;

public class AuthenticateHandler {

	
	public AuthenticateHandler initAuth(Router router, Vertx vertx) {
		
		SessionStore store = LocalSessionStore.create(vertx,"DKANBAN");
		SessionHandler sessionHandler = SessionHandler.create(store);
		
		router.route().handler(sessionHandler);
		return this;
	}
	
}
