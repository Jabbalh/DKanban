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
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import kanban.web.services.ISessionService;

public class FrontVerticle extends AbstractVerticle {

	
	private SockBusServer sockBuServer;
	@Inject
	private ISessionService sessionService;
	
	@Override	
	public void start() {
		
		Router router = Router.router(vertx);
		
		AuthenticateHandler authHandler = new AuthenticateHandler(sessionService).initAuth(router, vertx);
		
		KanbanHandler kanbanHandler = new KanbanHandler();
		UserHandler userHandler = new UserHandler();
		TicketHandler ticketHandler = new TicketHandler(sessionService);
		ApplicationHandler appHandler = new ApplicationHandler();
		
		/**
		 * Création des routes static pour les resources JS/CSS/etc
		 */
		router.route("/asset/*").handler(StaticHandler.create().setWebRoot("public/asset").setCachingEnabled(false));
		router.route("/app/*").handler(StaticHandler.create().setWebRoot("public/app").setCachingEnabled(false));
		/**
		 * Création des routes static pour les fichiers applicatif front (JS/HTML)
		 */
		//router.route("/app/*").handler(StaticHandler.create().setWebRoot("public/app").setCachingEnabled(false));
		
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
	          // IE8+ do not allow opening of attachments in the context of this resource
	          .putHeader("X-Download-Options", "noopen")
	          // enable XSS for IE
	          .putHeader("X-XSS-Protection", "1; mode=block")
	          // deny frames
	          .putHeader("X-FRAME-OPTIONS", "DENY");	
			context.next();
		});
		
		
		/**
		 * Gestion de l'authentification 
		 */		
		router.post("/public/login").handler(authHandler::loginHandler);
		router.get("/public/is/auth").handler(authHandler::isAuth);
		
		/**
		 * Ajout des routes qui consomment du JSON
		 */
		router.route("/api/ticket/update/*").consumes("application/json");
		
		/**
		 * ####### Routes relatives à la gestion des tickets #######  
		 */		
		
		/**
		 * Mise à jour 
		 */		
		router.post("/api/ticket/update/zone").handler(ticketHandler::apiTicketUpdateZone);
		
		router.post("/api/ticket/update/all").handler(ticketHandler::apiTicketUpdateAll);
		
		/**
		 * On renvois la liste des tickets par login
		 */
		router.get("/api/ticket/by/user/:login").handler(ticketHandler::apiTicketByUser);		
		
		/**
		 * On renvois la liste des tickets
		 */
		router.get("/api/ticket/list").handler(ticketHandler::apiTicketList);
		
		/**
		 * Création d'un ticket vide
		 */
		router.get("/api/ticket/new/empty").handler(ticketHandler::apiNewEmpty);
		
		router.post("/api/ticket/update/archive").handler(ticketHandler::apiArchive);
		
		/**
		 * ####### Routes relatives à la gestion des utilisateurs #######  
		 */
		
		/**
		 * On renvois la liste des utilisateurs
		 */
		router.get("/api/user/list").handler(userHandler::apiUserList);
		
		router.get("/api/user/:login").handler(userHandler::apiUserByLogin);
		
		router.post("/public/user/authenticate").handler(x -> authHandler.userAuthenticate(vertx,x));
		
		/**
		 * ####### Routes relatives à la gestion des applications #######  
		 */
		router.get("/api/application/list").handler(appHandler::apiApplicationList);
		router.get("/api/state/list").handler(appHandler::apiStateList);
		
		/**
		 * ####### Routes relatives à la gestion du kanban #######  
		 */
		
		/**
		 * On renvois la liste des tickets par login et par State
		 */
		router.get("/api/kanban/by/user/:user").handler(kanbanHandler::apiKanbanByUser);
		
		/**
		 * On renvois les headers du Kanban
		 */
		router.get("/api/kanban/headers").handler(kanbanHandler::apiKanbanHeaders);
				
		
		
		/**
		 * Création de l'écoute sur le port du server web
		 */
		//HttpServerOptions options = new HttpServerOptions();
		//options.setSsl(true);		
		
		vertx.createHttpServer().requestHandler(router::accept).listen(8080, x-> System.out.println("Liste en 8080 -> " + x.succeeded()));	
		
		/**
		 * Initialisation du Socket pour l'écoute via WebSocket
		 */
		sockBuServer = new SockBusServer(vertx,router);
		sockBuServer.initSokJs();
		
	}
	
	
	
}

