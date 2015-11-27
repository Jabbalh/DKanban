package kanban.service;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import kanban.bus.constants.EventBusNames;
import kanban.bus.constants.Sort;
import kanban.entity.db.User;
import kanban.service.contract.IMongoService;

public class VerticleUserService extends AbstractVerticle {

	@Inject
	private IMongoService mongoService;
	
	@Override	
	public void start(){
		vertx.eventBus().consumer(EventBusNames.USER_LIST, 			(Message<String> m) -> userList(m));
		vertx.eventBus().consumer(EventBusNames.USER_FIND_BY_LOGIN, (Message<String> m) -> userFindByLogin(m));
	}
	
	/**
	 * Liste des utilisateurs
	 * @param message
	 */
	private void userList(Message<String> message) {
		JsonObject sort = new JsonObject().put("sort", "firstName");
		mongoService.findAll( User.class, sort,Sort.ASC ,x -> message.reply(Json.encodePrettily(x)));
	}
	
	private void userFindByLogin(Message<String> message){
		mongoService.findOne(User.class, new JsonObject().put("login", message.body()))
		.when(x -> 
		{		
			message.reply(Json.encodePrettily(x));
		});
	}
	
}

