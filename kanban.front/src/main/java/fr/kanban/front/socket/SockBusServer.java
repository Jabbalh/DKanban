package fr.kanban.front.socket;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class SockBusServer {

	private static final Logger logger = LoggerFactory.getLogger(SockBusServer.class);
	
	private Vertx vertx;
	private Router router;
	
	public SockBusServer(Vertx vertx, Router router){
		this.vertx = vertx;
		this.router = router;
	}
	
	
	public void initSokJs(){
		
	    BridgeOptions options = new BridgeOptions()
	    		.addOutboundPermitted(new PermittedOptions().setAddress("update-card"))
	    		.addOutboundPermitted(new PermittedOptions().setAddress("insert-card"))
	    		.addOutboundPermitted(new PermittedOptions().setAddress("delete-card"));
	    		

	    router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(options, event -> {

	      // You can also optionally provide a handler like this which will be passed any events that occur on the bridge
	      // You can use this for monitoring or logging, or to change the raw messages in-flight.
	      // It can also be used for fine grained access control.

	      if (event.type() == BridgeEventType.SOCKET_CREATED) {
	    	  logger.debug("A socket was created");
	      }

	      // This signals that it's ok to process the event
	      event.complete(true);

	    }));
	    
	    router.route().handler(context -> {
			context.response().headers().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
			context.response().headers().add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
			context.next();
		});
	    
	    
	}
}
