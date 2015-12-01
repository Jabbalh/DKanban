package fr.kanban.front.auth;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;

public class AuthenticateHandler {

	public JWTAuth authProvider = null;
	
	public AuthenticateHandler initAuth(Router router, Vertx vertx) {
		
		SessionStore store = LocalSessionStore.create(vertx,"DKANBAN");
		SessionHandler sessionHandler = SessionHandler.create(store);
		
		router.route().handler(sessionHandler);
		
		
		JsonObject config = new JsonObject().put("keyStore", new JsonObject()
			    .put("path", "D:/Projets/Kanban/workspace/DKanban/key/keystore.jceks")
			    .put("type", "jceks")
			    .put("password", "secret"));		
		authProvider = JWTAuth.create(vertx, config);
		

		return this;
	}
	
}
