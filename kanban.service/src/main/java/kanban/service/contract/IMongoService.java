package kanban.service.contract;

import java.util.List;
import java.util.function.Consumer;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import kanban.bus.constants.Sort;
import kanban.utils.callback.Then;

public interface IMongoService {

	<T> void insert(T entity, Handler<AsyncResult<String>> callback);

	

	void delete(String index, Runnable afterDelete);



	<T> void findAll(Class<T> clazz, Consumer<List<T>> callBack);



	<T> void findAll(Class<T> clazz, JsonObject query, Consumer<List<T>> callBack);



	<T> void findAll(Class<T> clazz, JsonObject query, Sort sort, Consumer<List<T>> callBack);



	<T> Then<T> findOne(Class<T> clazz, JsonObject request);



	void update(String index, JsonObject query, JsonObject update,Consumer<Boolean> callback);



	<T> void update(T entity, Consumer<Boolean> callback);



	void reinitCounters();



	<T> Then<Integer> getNextSequence(Class<T> clazz);


	<T,R> Then<List<R>> findInternListFromObject(Class<T> clazz, Class<R> clazzR, JsonObject query, JsonObject fields);




}
