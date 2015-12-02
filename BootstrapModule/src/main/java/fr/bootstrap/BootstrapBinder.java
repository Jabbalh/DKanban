package fr.bootstrap;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import kanban.service.contract.ICryptoService;
import kanban.service.contract.IMongoService;
import kanban.service.impl.CryptoService;
import kanban.service.impl.MongoService;
import kanban.web.services.ISessionService;
import kanban.web.services.SessionService;

public class BootstrapBinder extends AbstractBinder {

	private static final Logger logger = LoggerFactory.getLogger(BootstrapBinder.class);
	
    @Override
    protected void configure() {
    	
    	logger.debug("configure binder");
    	bind(MongoService.class).to(IMongoService.class).in(Singleton.class);
    	bind(SessionService.class).to(ISessionService.class).in(Singleton.class);
    	bind(CryptoService.class).to(ICryptoService.class).in(Singleton.class);
    	
        // Configure bindings
        //bind(PropertiesConfigValueManager.class).to(ConfigValueManager.class).in(Singleton.class);

        // Install other binders
        //install(new OtherBinder1(), new OtherBinder2());

    }

}
