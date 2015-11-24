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
import kanban.service.utils.DbUtils;

public class VerticleUserService extends AbstractVerticle {

	@Inject
	private IMongoService mongoService;
	
	@Override	
	public void start(){
		vertx.eventBus().consumer(EventBusNames.USER_LIST, 	(Message<String> m) -> userList(m));
	}
	
	/**
	 * Liste des utilisateurs
	 * @param message
	 */
	private void userList(Message<String> message) {
		JsonObject sort = new JsonObject().put("sort", "firstName");
		mongoService.findAll(DbUtils.index(User.class), User.class, sort,Sort.ASC ,x -> message.reply(Json.encodePrettily(x)));
	}
	
}
