package fr.kanban.front.auth;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import kanban.bus.constants.EventBusNames;
import kanban.db.entity.User;
import kanban.entity.session.SessionData;
import kanban.web.services.ISessionService;

public class AuthenticateHandler {

	private JWTAuth authProvider = null;
		
	private ISessionService sessionService;
	
	
	public AuthenticateHandler(ISessionService sessionService){
		this.sessionService = sessionService;
	}
	
	public AuthenticateHandler initAuth(Router router, Vertx vertx) {
		router.route().handler(CookieHandler.create());
		SessionStore store = LocalSessionStore.create(vertx);
		SessionHandler sessionHandler = SessionHandler.create(store);
		sessionHandler.setSessionTimeout(14400000);
		router.route().handler(sessionHandler);
		
		
		JsonObject config = new JsonObject().put("keyStore", new JsonObject()
			    .put("path", "key/keystore.jceks")
			    .put("type", "jceks")
			    .put("password", "secret"));		
		authProvider = JWTAuth.create(vertx, config);
		
		this.internalInit(router, vertx);

		return this;
	}
	
	private void internalInit(Router router, Vertx vertx){			
		router.route("/api/*").handler(context -> {
			Boolean ok = (sessionService.isAuthenticate(context.session(), context.request()));			
				if (ok) 	context.next();
				else 		context.fail(401);						
		});
		
		router.route("/api/*").failureHandler(failureRoutingContext ->{
			  int statusCode = failureRoutingContext.statusCode();		
			  if (statusCode <=0) {				  
				  statusCode = 404;
			  }
			  failureRoutingContext.response().setStatusCode(statusCode).end(Json.encodePrettily("Not Authorized"));
			});
		
		JWTAuthHandler authRouteHandler = JWTAuthHandler.create(authProvider);
		
		router.route("/api/*").handler(authRouteHandler);
		//router.route("/app/*").handler(authRouteHandler);
		
	}
	
	public void loginHandler(RoutingContext context){
		JsonObject logForm = context.getBodyAsJson();
		
		String login = logForm.getString("l");
		//String password = logForm.getString("p");
		
		SessionData data = new SessionData();
		data.setCurrentUser(new User(login, "NA", login, login));
		data.setToken(authProvider.generateToken(
				new JsonObject().put("user",new JsonObject(Json.encodePrettily(data.getCurrentUser()))), new JWTOptions()));
		sessionService.toSession(context.session(), data);
										
		context.response().end(data.getToken());
	}
	
	/**
	 * Test de l'authentification
	 * @param context
	 */
	public void userAuthenticate(Vertx vertx,RoutingContext context){
		JsonObject user = context.getBodyAsJson();
		vertx.eventBus().send(EventBusNames.USER_AUTHENTICATE, user, x -> {
			Boolean b = Boolean.parseBoolean(x.result().body().toString());
			if (b){
				String login = user.getString("login");
				SessionData data = new SessionData();
				data.setCurrentUser(new User(login, "NA", login, login));
				data.setToken(authProvider.generateToken(
						new JsonObject().put("user",new JsonObject(Json.encodePrettily(data.getCurrentUser()))), new JWTOptions()));
				sessionService.toSession(context.session(), data);											
				context.response().end(Json.encodePrettily(data.getToken()));
			} else {
				
				context.response().end(Json.encodePrettily("KO"));
			}
			
		});
	}
	
	public void signOut(RoutingContext context){
		sessionService.signOut(context.session(), context.request());	
		context.response().end();
	}
	
	/**
	 * Test de l'authentification
	 * @param context
	 */
	public void isAuth(RoutingContext context) {
		context.response().end(new JsonObject().put("auth", sessionService.isAuthenticate(context.session(), context.request())).encodePrettily());
		
	}

	public void currentUser(RoutingContext context) {
		context.response().end(Json.encode(new JsonObject().put("currentUser",sessionService.getCurrentUser(context.session()).getLogin())));
	}

}
