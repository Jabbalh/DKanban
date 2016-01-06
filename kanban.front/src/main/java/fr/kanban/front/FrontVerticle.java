package fr.kanban.front;

import javax.inject.Inject;

import fr.kanban.front.application.ApplicationHandler;
import fr.kanban.front.auth.AuthenticateHandler;
import fr.kanban.front.kanban.KanbanHandler;
import fr.kanban.front.socket.SockBusServer;
import fr.kanban.front.ticket.TicketHandler;
import fr.kanban.front.user.UserHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import kanban.bus.constants.EventBusNames;
import kanban.db.entity.StatutParameter;
import kanban.db.entity.User;
import kanban.utils.log.Logger;
import kanban.web.services.ISessionService;

public class FrontVerticle extends AbstractVerticle {

	private static final Logger logger = Logger.Get(FrontVerticle.class);

	private SockBusServer sockBuServer;
	@Inject
	private ISessionService sessionService;

	@Override
	public void start() {

		Router router = Router.router(vertx);

		AuthenticateHandler authHandler	= new AuthenticateHandler(sessionService).initAuth(router, vertx);
		KanbanHandler kanbanHandler 	= new KanbanHandler();
		UserHandler userHandler			= new UserHandler();
		TicketHandler ticketHandler		= new TicketHandler(sessionService);
		ApplicationHandler appHandler	= new ApplicationHandler();

		/**
		 * Création des routes static pour les resources JS/CSS/etc
		 */
		router.route("/asset/*")	.handler(StaticHandler.create().setWebRoot("public/asset").setCachingEnabled(false));
		router.route("/app/*")		.handler(StaticHandler.create().setWebRoot("public/app").setCachingEnabled(false));

		/**
		 * Ajout du type de renvois pour les responses
		 */
		router.route("/api/*").produces("application/json");
		router.route("/public/*").produces("application/json");
		router.route().handler(BodyHandler.create());
		router.route().handler(context -> {
			context.response().headers().add(HttpHeaders.CONTENT_TYPE, "application/json");
			context.response().headers().add("content-type", "text/html;charset=UTF-8");

			context.response()
					// do not allow proxies to cache the data
					.putHeader("Cache-Control", "no-store, no-cache")
					// prevents Internet Explorer from MIME - sniffing a
					// response away from the declared content-type
					.putHeader("X-Content-Type-Options", "nosniff")
					// Strict HTTPS (for about ~6Months)
					.putHeader("Strict-Transport-Security", "max-age=" + 15768000)
					// IE8+ do not allow opening of attachments in the context
					// of this resource
					.putHeader("X-Download-Options", "noopen")
					// enable XSS for IE
					.putHeader("X-XSS-Protection", "1; mode=block")
					// deny frames
					.putHeader("X-FRAME-OPTIONS", "DENY");

			logger.debug(() -> "handle -> " + context.request().path());

			context.next();
		});

		
		// routes pour les tickets
		handleRouteForTicket(router, ticketHandler);
		// routes pour les users
		handleRouteForUser(router,userHandler);
		// routes pour l'authentification
		handleRouteForAuth(router, authHandler);				
		//Routes relatives à l'administration		 
		handleRouteForAdmin(router, appHandler);		
		// Routes relatives à la gestion du kanban
		handleRouteForKanban(router, appHandler,kanbanHandler);
		

		/**
		 * Création de l'écoute sur le port du server web
		 */
		vertx.createHttpServer().requestHandler(router::accept).listen(8080,x -> logger.info(() -> "Liste en 8080 -> " + x.succeeded()));

		/**
		 * Initialisation du Socket pour l'écoute via WebSocket
		 */
		sockBuServer = new SockBusServer(vertx, router);
		sockBuServer.initSokJs();

	}
	
	/**
	 * Routes relative aux tickets
	 * @param router
	 * @param ticketHandler
	 */
	private void handleRouteForTicket(Router router, TicketHandler ticketHandler) {
		
		/**
		 * Ajout des routes qui consomment du JSON
		 */
		router.route("/api/ticket/update/*").consumes("application/json");
		
		router.post("/api/ticket/update/zone")		.handler(ticketHandler::apiTicketUpdateZone);
		router.post("/api/ticket/update/all")		.handler(ticketHandler::apiTicketUpdateAll);
		router.post("/api/ticket/search")			.handler(ticketHandler::apiTicketSearch);
		router.post("/api/ticket/delete")			.handler(ticketHandler::apiTicketDelete);
		router.post("/api/ticket/update/archive")	.handler(ticketHandler::apiArchive);

		
		router.get("/api/ticket/by/user/:login")	.handler(ticketHandler::apiTicketByUser);
		router.get("/api/ticket/list")				.handler(ticketHandler::apiTicketList);
		router.get("/api/ticket/new/empty")			.handler(ticketHandler::apiNewEmpty);

		
	}

	private void handleRouteForAdmin(Router router, ApplicationHandler appHandler){
		
		router.post("/api/app/save")				.handler(x -> appHandler.apiSet(x, EventBusNames.APPLICATION_SAVE, x.getBodyAsJson().getJsonObject("data")));
		router.post("/api/state/save")				.handler(x -> appHandler.apiSet(x, EventBusNames.STATE_SAVE, x.getBodyAsJson().getJsonObject("data")));
		router.post("/api/state/insert")			.handler(x -> appHandler.apiSet(x, EventBusNames.STATE_INSERT, x.getBodyAsJson().getJsonObject("data")));
		router.post("/api/zone/save")				.handler(x -> appHandler.apiSet(x, EventBusNames.ZONE_SAVE, x.getBodyAsJson().getJsonObject("data")));
		router.post("/api/priority/save")				.handler(x -> appHandler.apiSet(x, EventBusNames.PRIORITY_SAVE, x.getBodyAsJson().getJsonObject("data")));
		router.post("/api/priority/insert")			.handler(x -> appHandler.apiSet(x, EventBusNames.PRIORITY_INSERT, x.getBodyAsJson().getJsonObject("data")));
		
		router.post("/api/global/title")			.handler(x -> appHandler.apiSet(x, EventBusNames.GLOBAL_TITLE_SET));
		
		
		
		router.get("/api/admin/zone/list")			.handler(x -> appHandler.apiGet(x, EventBusNames.ADMIN_ZONE_LIST));
		router.get("/api/admin/user/list")			.handler(x -> appHandler.apiGet(x, EventBusNames.ADMIN_USER_LIST));
		router.get("/api/admin/application/list")	.handler(x -> appHandler.apiGet(x, EventBusNames.ADMIN_APP_LIST));
		router.get("/api/admin/state/list")			.handler(x -> appHandler.apiGet(x, EventBusNames.ADMIN_STATUT_LIST));
		router.get("/api/admin/priority/list")		.handler(x -> appHandler.apiGet(x, EventBusNames.ADMIN_PRORITY_LIST));
		
		router.get("/public/global/title")			.handler(x -> appHandler.apiGet(x, EventBusNames.GLOBAL_TITLE_GET));		
		router.get("/public/dev/kanban")			.handler(x -> appHandler.apiGet(x, EventBusNames.KANBAN_FULL));
		
		
		router.post("/api/state/delete")			.handler(x -> appHandler.apiDelete(x,StatutParameter.class.getName() , x.getBodyAsJson().getJsonObject("data")));
		
		
	}
	
	/**
	 * Routes relative au kanban
	 * @param router
	 * @param appHandler
	 */
	private void handleRouteForKanban(Router router, ApplicationHandler appHandler, KanbanHandler kanbanHandler) {
		router.get("/api/application/list")			.handler(x -> appHandler.apiGet(x, EventBusNames.APPLICATION_LIST));
		router.get("/api/state/list")				.handler(x -> appHandler.apiGet(x, EventBusNames.STATE_LIST));
		router.get("/api/zone/list")				.handler(x -> appHandler.apiGet(x, EventBusNames.ZONE_LIST));
		
		//On renvois la liste des tickets par login non archivé
		router.get("/api/kanban/by/user/:user")		.handler(kanbanHandler::apiKanbanByUser);
		router.get("/api/kanban/headers")			.handler(kanbanHandler::apiKanbanHeaders);
	}


	/**
	 * ####### Routes relatives à la gestion de l'authentification #######
	 * @param router
	 * @param authHandler
	 */
	private void handleRouteForAuth(Router router, AuthenticateHandler authHandler) {
		router.post("/public/user/authenticate")	.handler(x -> authHandler.userAuthenticate(vertx, x));
		router.post("/public/login")				.handler(authHandler::loginHandler);
		
		router.get("/api/signout")					.handler(authHandler::signOut);		
		router.get("/public/is/auth")				.handler(authHandler::isAuth);	
	}

	
	/**
	 * ####### Routes relatives à la gestion des utilisateurs #######
	 */
	private void handleRouteForUser(Router router, UserHandler userHandler){
		router.post("/api/user/save")				.handler(userHandler::userSave);
		router.post("/api/user/insert")				.handler(userHandler::apiUserInsert);
		/**
		 * On renvois la liste des utilisateurs
		 */
		router.get("/api/user/list")				.handler(userHandler::apiUserList);
		router.get("/api/user/:login")				.handler(userHandler::apiUserByLogin);		
		router.get("/public/user/new")					.handler(context -> context.response().end(Json.encodePrettily(new User())));
		
	}
	
	

}
