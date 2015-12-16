package kanban.service.contract;

import java.util.List;
import java.util.function.Consumer;

import io.vertx.core.json.JsonObject;
import kanban.bus.constants.Sort;
import kanban.utils.callback.MongoCallBack;

public interface IMongoService {

	<T> MongoCallBack<Boolean> insert(T entity);

	

	void delete(String index, Runnable afterDelete);



	<T> MongoCallBack<List<T>> findAll(Class<T> clazz);



	<T> MongoCallBack<List<T>> findAll(Class<T> clazz, JsonObject query);



	<T> MongoCallBack<List<T>> findAll(Class<T> clazz, JsonObject query, Sort sort);



	<T> MongoCallBack<T> findOne(Class<T> clazz, JsonObject request);



	<T> MongoCallBack<Boolean> update(String index, JsonObject query, JsonObject update);



	<T> MongoCallBack<Boolean> update(T entity);



	void reinitCounters();



	<T> MongoCallBack<Integer> getNextSequence(Class<T> clazz);


	<T,R> MongoCallBack<List<R>> findInternListFromObject(Class<T> clazz, Class<R> clazzR, JsonObject query, JsonObject fields);



	void createIndex(Consumer<Boolean> callback);



	<T> MongoCallBack<List<T>> fullSearchCommand(Class<T> index, JsonObject command);



	<T> MongoCallBack<Boolean> deleteEntity(Class<T> clazz, String id);




}
