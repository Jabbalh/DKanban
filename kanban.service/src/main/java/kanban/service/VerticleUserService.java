package kanban.service;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import kanban.bus.constants.EventBusNames;
import kanban.bus.constants.Sort;
import kanban.entity.db.User;
import kanban.service.contract.ICryptoService;
import kanban.service.contract.IMongoService;
import kanban.service.utils.BusUtils;
import kanban.utils.callback.Async;

public class VerticleUserService extends AbstractVerticle {

	@Inject
	private IMongoService mongoService;
	
	@Inject
	private ICryptoService cryptoService;
	
	@Override	
	public void start(){
		vertx.eventBus().consumer(EventBusNames.USER_LIST, 			(Message<String> m) -> userList(m));
		vertx.eventBus().consumer(EventBusNames.USER_FIND_BY_LOGIN, (Message<String> m) -> userFindByLogin(m));
		vertx.eventBus().consumer(EventBusNames.USER_AUTHENTICATE, (Message<JsonObject> m) -> authenticate(m));
	}
	
	/**
	 * Liste des utilisateurs
	 * @param message
	 */
	private void userList(Message<String> message) {
		JsonObject sort = new JsonObject().put("sort", "firstName");
		Async.When(() -> mongoService.findAll( User.class, sort,Sort.ASC)).doThat(x -> message.reply(Json.encodePrettily(x)));
	}
	
	/**
	 * Recherche d'un utilisateur par le login
	 * @param message
	 */
	private void userFindByLogin(Message<String> message){
		Async.When(()-> mongoService.findOne(User.class, new JsonObject().put("login", message.body())))
		.doThat(x -> 
		{		
			message.reply(Json.encodePrettily(x));
		});
	}
	
	/**
	 * Authentification d'un utilisateur
	 * @param message
	 */
	private void authenticate(Message<JsonObject> message){
		JsonObject user = message.body();
		String password = user.getString("password");
		String login = user.getString("login");
		
		vertx.eventBus().send(EventBusNames.USER_FIND_BY_LOGIN, login, x->{
			if (x.succeeded() && BusUtils.isNotNull(x.result().body())){
				User u = Json.decodeValue(x.result().body().toString(), User.class);
				Boolean result = cryptoService.compareWithHash256(password, u.getPassword());
				message.reply(result);
			} else {
				message.reply(false);
			}
			
		});
		
		
	}
	
}

