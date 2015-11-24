package kanban.service.contract;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.function.Consumer;

import kanban.bus.constants.Sort;

public interface IMongoService {

	<T> void insert(String index, T entity, Handler<AsyncResult<String>> callback);

	

	void delete(String index, Runnable afterDelete);



	<T> void findAll(String index, Class<T> clazz, Consumer<List<T>> callBack);



	<T> void findAll(String index, Class<T> clazz, JsonObject query, Consumer<List<T>> callBack);



	<T> void findAll(String index, Class<T> clazz, JsonObject query, Sort sort, Consumer<List<T>> callBack);



	<T> void findOne(String index, Class<T> clazz, JsonObject request,Consumer<T> callback);



	void update(String index, JsonObject query, JsonObject update,Consumer<Boolean> callback);




}
