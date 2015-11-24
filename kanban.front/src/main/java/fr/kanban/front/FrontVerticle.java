package fr.kanban.front;

import fr.kanban.front.kanban.KanbanHandler;
import fr.kanban.front.socket.SockBusServer;
import fr.kanban.front.ticket.TicketHandler;
import fr.kanban.front.user.UserHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class FrontVerticle extends AbstractVerticle {

	
	private SockBusServer sockBuServer;
	private KanbanHandler kanbanHandler;
	private TicketHandler ticketHandler;
	private UserHandler userHandler;
	
	@Override	
	public void start() {
		
		Router router = Router.router(vertx);
		
		
		kanbanHandler = new KanbanHandler();
		userHandler = new UserHandler();
		ticketHandler = new TicketHandler();
		
		/**
		 * Création des routes static pour les resources JS/CSS/etc
		 */
		router.route("/asset/*").handler(StaticHandler.create().setWebRoot("public/asset").setCachingEnabled(false));
		
		/**
		 * Création des routes static pour les fichiers applicatif front (JS/HTML)
		 */
		router.route("/app/*").handler(StaticHandler.create().setWebRoot("public/app").setCachingEnabled(false));
		
		/**
		 * Ajout du type de renvois pour les responses
		 */
		router.route().produces("application/json");
		router.route().handler(BodyHandler.create());
		router.route().handler(context -> {
			context.response().headers().add(HttpHeaders.CONTENT_TYPE, "application/json");
			context.next();
		});
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
		router.put("/api/ticket/update").handler(ticketHandler::apiTicketUpdate);
		
		/**
		 * On renvois la liste des tickets par login
		 */
		router.get("/api/ticket/by/user/:login").handler(ticketHandler::apiTicketByUser);		
		
		/**
		 * On renvois la liste des tickets
		 */
		router.get("/api/ticket/list").handler(ticketHandler::apiTicketList);
		
		/**
		 * ####### Routes relatives à la gestion des utilisateurs #######  
		 */
		
		/**
		 * On renvois la liste des utilisateurs
		 */
		router.get("/api/user/list").handler(userHandler::apiUserList);
		
		
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
		
		vertx.createHttpServer().requestHandler(router::accept).listen(8080);	
		
		/**
		 * Initialisation du Socket pour l'écoute via WebSocket
		 */
		sockBuServer = new SockBusServer(vertx,router);
		sockBuServer.initSokJs();
		
	}
	
	
	
}

