package kanban.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import kanban.bus.constants.Sort;
import kanban.service.contract.IMongoService;

public class MongoService implements IMongoService {

	private MongoClient mongoClient;
	
	
	private Vertx vertx;
	
	public MongoService() {
		this.vertx = Vertx.currentContext().owner();
		this.initMongo();
	}
	
	private void initMongo(){		
		if (mongoClient == null){
			System.out.println("Initialisation du client mongo");
			JsonObject mongoConf = new JsonObject();
			mongoConf.put("host", "127.0.0.1");
			mongoConf.put("port", 27017);
			mongoConf.put("db_name", "kanban");
			mongoClient = MongoClient.createShared(vertx, mongoConf);
		}
	}
	
	@Override
	public void update(String index, JsonObject query,JsonObject update, Consumer<Boolean> callback){		
		mongoClient.update(index, query, update, x -> {
			callback.accept(x.succeeded());
		});
	}
	
	@Override
	public void delete(String index, Runnable afterDelete) {
		mongoClient.dropCollection(index, x -> {
			if (x.succeeded()){
				System.out.println("Deletion SUCCEEDED for " + index + " run after ... ");
				afterDelete.run();
			}
			else {
				System.out.println("###############Deletion FAILED for " + index);
				System.out.println(x.cause());
			}
			
		});
	}
	
	@Override
	public <T> void findOne(String index, Class<T> clazz, JsonObject request,Consumer<T> callback) {
		mongoClient.findOne(index, request, null, x -> {
			
			if (x.succeeded()){
				callback.accept(Json.decodeValue(x.result().encodePrettily(), clazz));
			} else {
				System.out.println("findOne ->" + index + " on error -> " + x.cause());
			}
		});
	}
	
	
	@Override
	public <T> void insert(String index, T entity, Handler<AsyncResult<String>> callback){
		String json = Json.encode(entity);
		JsonObject jsonEntity = new JsonObject(json);
		mongoClient.insert(index, jsonEntity, callback );	
		
	}
	
	@Override
	public <T> void findAll(String index,Class<T> clazz, Consumer<List<T>> callBack) {
		
		findAll(index, clazz, new JsonObject(), callBack);
	}
	
	@Override
	public <T> void findAll(String index,Class<T> clazz, JsonObject query, Consumer<List<T>> callBack) {
		findAll(index, clazz, query, null, callBack);
	}
	
	@Override
	public <T> void findAll(String index,Class<T> clazz, JsonObject query,Sort sort, Consumer<List<T>> callBack) {
		
		FindOptions options = new FindOptions();
		Object sortJson = query.getValue("sort");
		if (sortJson != null) {
			if (sort == null) sort = Sort.ASC;
			System.out.println("Sort -> " + sortJson + " -> " + sort.value());
			query.remove("sort");
			JsonObject jsonSort = new JsonObject();
			JsonObject s = new JsonObject();
			s.put(sortJson.toString(), sort.value());
			jsonSort.put("sort", s);
			options.setSort(s);
		}
		
		mongoClient.findWithOptions(index, query,options, x -> {
			if (x.succeeded()) {
				List<T> liste = new ArrayList<>();					
				for (JsonObject item : x.result()){
					T result = Json.decodeValue(item.encodePrettily(), clazz);
					liste.add(result);
				}
				callBack.accept(liste);
			} else {
				System.out.println("ERROR : " + x.cause());
				callBack.accept(new ArrayList<>());
			}
			
		});
	}
	
	
}
