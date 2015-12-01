package kanban.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import kanban.bus.constants.EventBusNames;
import kanban.entity.db.Application;
import kanban.entity.db.StateTicket;
import kanban.service.contract.IMongoService;

public class VerticleApplicationService extends AbstractVerticle {

	@Inject
	private IMongoService mongoService;
	
	@Override
	public void start(){
		
		vertx.eventBus().consumer(EventBusNames.APPLICATION_LIST, (Message<String> x) -> appList(x));
		vertx.eventBus().consumer(EventBusNames.STATE_LIST, (Message<String> x) -> stateList(x));
	}
	
	private void appList(Message<String> message){
		mongoService.findAll(Application.class, x -> {
			List<String> result = x.stream().map(a -> a.getName()).sorted().collect(Collectors.toList());
			message.reply(Json.encode(result));
		});
	}
	
	private void stateList(Message<String> message){
		mongoService.findAll(StateTicket.class, x -> {
			List<StateTicket> result = x.stream()
						.sorted((c1,c2) -> c1.getLibelle().compareToIgnoreCase(c2.getLibelle()))
						.collect(Collectors.toList());
			message.reply(Json.encode(result));
		});
	}
	
}
