package fr.bootstrap;

import javax.inject.Singleton;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import kanban.service.contract.IMongoService;
import kanban.service.impl.MongoService;

public class BootstrapBinder extends AbstractBinder {

    @Override
    protected void configure() {
    	
    	System.out.println("configure binder");
    	bind(MongoService.class).to(IMongoService.class).in(Singleton.class);
    	
    	
        // Configure bindings
        //bind(PropertiesConfigValueManager.class).to(ConfigValueManager.class).in(Singleton.class);

        // Install other binders
        //install(new OtherBinder1(), new OtherBinder2());

    }

}
