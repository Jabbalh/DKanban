package kanban.service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import kanban.bus.constants.EventBusNames;
import kanban.bus.constants.Sort;
import kanban.db.entity.ParamTuple;
import kanban.db.entity.User;
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
		vertx.eventBus().consumer(EventBusNames.USER_LIST, 			(Message<String> m) -> fullUserList(m, 
						users -> users
							.stream().map(a -> new ParamTuple(a.getLogin(), a.getFirstName() + " " + a.getLastName()))
							.collect(Collectors.toList()),false));
		vertx.eventBus().consumer(EventBusNames.USER_SAVE, 			this::userSave);
		vertx.eventBus().consumer(EventBusNames.USER_INSERT, 		this::userInsert);
		
		vertx.eventBus().consumer(EventBusNames.USER_FIND_BY_LOGIN, this::userFindByLogin);
		vertx.eventBus().consumer(EventBusNames.USER_AUTHENTICATE, this::authenticate);
		vertx.eventBus().consumer(EventBusNames.ADMIN_USER_LIST, (Message<String> x) -> fullUserList(x, u -> u));
	}
	
	
	
	private void userSave(Message<JsonObject> message) {
		User user = Json.decodeValue(message.body().encode(), User.class);	
		Async.When(() -> mongoService.findOne(User.class, new JsonObject().put("_id", user.get_id())))
		.doThat(userFind -> {
			user.setPassword(userFind.getPassword());
			Async.When(() -> mongoService.update(user)).doThat(c -> message.reply(Json.encode(c)));	
		});
		
	}
	
	private void userInsert(Message<JsonObject> message) {	
		User user = Json.decodeValue(message.body().encode(), User.class);		
		user.setPassword(cryptoService.genHash256(user.getPassword()));		
		Async.When(() -> mongoService.insert(user)).doThat(c -> message.reply(Json.encode(user)));
		
	}


	private <R> void fullUserList(Message<String> message, Function<List<User>, List<R>> transform, Boolean withDeleted){
		JsonObject query = new JsonObject().put("sort", "firstName");
		if (!withDeleted) query.put("deleted",false);

		Async.When(() -> mongoService.findAll( User.class, query,Sort.ASC))
				.doThat(users -> message.reply(Json.encode(transform.apply(users))));
	}

	private <R> void fullUserList(Message<String> message, Function<List<User>, List<R>> transform){
		fullUserList(message, transform,true);

	}
	
	
	
	
	/**
	 * Recherche d'un utilisateur par le login
	 * @param message
	 */
	private void userFindByLogin(Message<String> message){
		Async.When(()-> mongoService.findOne(User.class, new JsonObject().put("login", message.body())))
		.doThat(x -> message.reply(Json.encodePrettily(x)));
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

