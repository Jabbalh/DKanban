package kanban.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import kanban.bus.constants.Sort;
import kanban.entity.db.Ticket;
import kanban.service.contract.IMongoService;
import kanban.service.utils.DbUtils;
import kanban.utils.callback.Then;

public class MongoService implements IMongoService {

	private MongoClient mongoClient;
	
	private final static Logger logger = LoggerFactory.getLogger(MongoService.class);
	
	private Vertx vertx;
	
	public MongoService() {
		this.vertx = Vertx.currentContext().owner();
		this.initMongo();
	}
	
	private void initMongo(){		
		if (mongoClient == null){
			logger.debug("Initialisation du client mongo");
			JsonObject mongoConf = new JsonObject()
				.put("host", "127.0.0.1")
				.put("port", 27017)
				.put("db_name", "kanban");				
			mongoClient = MongoClient.createShared(vertx, mongoConf);
		}
	}
	
	@Override
	public void update(String index, JsonObject query,JsonObject update, Consumer<Boolean> callback){	
		logger.debug("MongoService.update.query -> " + query.encodePrettily());
		logger.debug("MongoService.update.update -> " + update.encodePrettily());
		mongoClient.update(index, query, update, x -> {
			logger.debug("MongoService.update.succeeded -> " + x.succeeded());
			logger.debug("MongoService.update.result -> " + x.result());
			if (x.failed()) logger.error("update " + index + " -> " + x.cause());
			callback.accept(x.succeeded());
		});
		
	}
	
	@Override
	public <T> void update(T entity, Consumer<Boolean> callback){
				
		mongoClient.save(DbUtils.index(entity.getClass()), new JsonObject(Json.encodePrettily(entity))  , x -> {
			logger.debug("MongoService.update.succeeded -> " + x.succeeded());
			logger.debug("MongoService.update.result -> " + x.result());
			
			if (x.failed()) logger.error("update " + entity.getClass().getName() + " -> " + x.cause());
			callback.accept(x.succeeded());
		});
	}
	
	@Override
	public void delete(String index, Runnable afterDelete) {
		mongoClient.dropCollection(index, x -> {
			if (x.succeeded()){
				logger.debug("Deletion SUCCEEDED for " + index + " run after ... ");
				afterDelete.run();
			}
			else {
				logger.error("###############Deletion FAILED for " + index);
				logger.error(x.cause());
			}
			
		});
	}
	
	@Override
	public <T> Then<T> findOne(Class<T> clazz, JsonObject request) {
		Then<T> then = new Then<>();
		mongoClient.findOne(DbUtils.index(clazz), request, null, x -> {
			T result = null;
			if (x.succeeded()){
				if (x.result() != null){
					result = Json.decodeValue(x.result().encodePrettily(), clazz);
				}				
			} else {
				logger.error("findOne ->" + DbUtils.index(clazz) + " on error -> " + x.cause());
			}
			then.apply(result);
		});
		return then;
	}
	
	@Override
	public <T,R> Then<List<R>> findInternListFromObject(Class<T> clazz,Class<R> clazzR, JsonObject query, JsonObject fields) {
		Then<List<R>> then = new Then<>();
		
		mongoClient.findOne(DbUtils.index(clazz), query, fields, x -> {
			
			logger.debug("findInternListFromObject -> " + x.result().encodePrettily());
			
			JsonArray array = x.result().getJsonArray("ticketHistory", new JsonArray());
			
			List<R> result = new ArrayList<>();
			if (array != null && array.getList().size()>0) {
				for (Object o : array){
					if (o instanceof JsonObject){
						
						result.add(Json.decodeValue(Json.encodePrettily(o), clazzR));
					}
				}
			}
			
			then.apply(result);
		});
		
		return then;
	}
	
	
	@Override
	public <T> void insert(T entity, Handler<AsyncResult<String>> callback){
		String json = Json.encode(entity);
		JsonObject jsonEntity = new JsonObject(json);
		mongoClient.insert(DbUtils.index(entity.getClass()), jsonEntity, callback );	
		
	}
	
	@Override
	public <T> void findAll(Class<T> clazz, Consumer<List<T>> callBack) {
		
		findAll(clazz, new JsonObject(), callBack);
	}
	
	@Override
	public <T> void findAll(Class<T> clazz, JsonObject query, Consumer<List<T>> callBack) {
		findAll( clazz, query, null, callBack);
	}
	
	@Override
	public <T> void findAll(Class<T> clazz, JsonObject query,Sort sort, Consumer<List<T>> callBack) {
		
		FindOptions options = new FindOptions();
		
		Object sortJson = query.getValue("sort");
		if (sortJson != null) {
			if (sort == null) sort = Sort.ASC;
			logger.debug("Sort -> " + sortJson + " -> " + sort.value());
			query.remove("sort");
			JsonObject jsonSort = new JsonObject();
			JsonObject s = new JsonObject();
			s.put(sortJson.toString(), sort.value());
			jsonSort.put("sort", s);
			options.setSort(s);
		}
		
		mongoClient.findWithOptions(DbUtils.index(clazz), query,options, x -> {
			if (x.succeeded()) {
				List<T> liste = new ArrayList<>();					
				for (JsonObject item : x.result()){
					T result = Json.decodeValue(item.encodePrettily(), clazz);
					liste.add(result);
				}
				callBack.accept(liste);
			} else {
				logger.error("ERROR : " + x.cause());
				callBack.accept(new ArrayList<>());
			}
			
		});
	}
	
	@Override
	public <T> Then<Integer> getNextSequence(Class<T> clazz){
		Then<Integer> then = new Then<>();
		mongoClient.update("counters", 
				new JsonObject().put("_id", DbUtils.index(clazz)), 
				new JsonObject().put("$inc", new JsonObject().put("seq", 1)), s -> {
					if (s.succeeded()){						
						mongoClient.findOne("counters", new JsonObject().put("_id", DbUtils.index(clazz)), null, i -> {
							then.apply(i.result().getInteger("seq"));
						});
					}
					
				});
		return then;
	}
	
	@Override
	public void reinitCounters() {
		mongoClient.dropCollection("counters", x -> {
			mongoClient.insert("counters", new JsonObject().put("_id", DbUtils.index(Ticket.class)).put("seq", 5), r -> {				
			});
		});
	}
	
	
}
