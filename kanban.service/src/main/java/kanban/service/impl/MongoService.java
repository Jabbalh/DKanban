package kanban.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import kanban.bus.constants.Sort;
import kanban.entity.db.Ticket;
import kanban.service.contract.IMongoService;
import kanban.service.utils.DbUtils;
import kanban.utils.callback.MongoCallBack;

public class MongoService implements IMongoService {

	private MongoClient mongoClient;
	
	private final static kanban.utils.log.Logger logger = kanban.utils.log.Logger.Get(MongoService.class);
	
	private Vertx vertx;
	
	public MongoService() {
		this.vertx = Vertx.currentContext().owner();
		this.initMongo();
	}
	
	private void initMongo(){		
		if (mongoClient == null){
			logger.debug(() -> "Initialisation du client mongo");
			JsonObject mongoConf = new JsonObject()
				.put("host", "127.0.0.1")
				.put("port", 27017)
				.put("db_name", "kanban");				
			mongoClient = MongoClient.createShared(vertx, mongoConf);
		}
	}
	
	@Override
	public MongoCallBack<Boolean> update(String index, JsonObject query,JsonObject update){	
		logger.debug(() ->"MongoService.update -> " + index);		
		MongoCallBack<Boolean> then = new MongoCallBack<>();
		mongoClient.update(index, query, update, x -> {
			logger.debug(() ->"MongoService.update.succeeded -> " + x.succeeded());			
			if (x.failed()) logger.error(() ->"update " + index + " -> " + x.cause());			
			//this.secureThenCall(then, x.succeeded());
			then.finish(x.succeeded());
		});
		return then;
		
	}
	
	/**
	 * Appels d'un apply sur un then avec log d'erreur
	 * @param then
	 * @param result
	 */
	private <T,R> void secureThenCall(MongoCallBack<T> then,T result){				
		then.finish(result);
	}
	
	@Override
	public <T> MongoCallBack<Boolean> update(T entity){
		
		MongoCallBack<Boolean> then = new MongoCallBack<>();
		
		
		/*mongoClient.replace(DbUtils.index(entity.getClass()), 
				new JsonObject().put("_id", entity.) query, replace, resultHandler)*/
		
		mongoClient.save(DbUtils.index(entity.getClass()), new JsonObject(Json.encodePrettily(entity))  , x -> {
			logger.debug(() -> "MongoService.update.succeeded -> " + x.succeeded());						
			if (x.failed()) logger.error(() -> "update " + entity.getClass().getName() + " -> " + x.cause());
			
			this.secureThenCall(then, x.succeeded());			
		});
		return then;
	}
	
	@Override
	public void delete(String index, Runnable afterDelete) {
		mongoClient.dropCollection(index, x -> {
			if (x.succeeded()){
				logger.debug(() -> "Deletion SUCCEEDED for " + index + " run after ... ");
				afterDelete.run();
			}
			else {
				logger.error(() -> "###############Deletion FAILED for " + index,x.cause());				
			}
			
		});
	}
	
	@Override
	public <T> MongoCallBack<Boolean> deleteEntity(Class<T> clazz, String id){
		MongoCallBack<Boolean> then = new MongoCallBack<>();		
		mongoClient.removeOne(DbUtils.index(clazz), new JsonObject().put("_id", id), x -> {
			then.finish(x.succeeded());
		});		
		
		return then;
	}
	
	@Override
	public <T> MongoCallBack<T> findOne(Class<T> clazz, JsonObject request) {
		System.out.println("MongoService.FindOne");
		MongoCallBack<T> then = new MongoCallBack<>();
		mongoClient.findOne(DbUtils.index(clazz), request, null, x -> {
			T result = null;
			if (x.succeeded()){
				if (x.result() != null){
					result = Json.decodeValue(x.result().encodePrettily(), clazz);
				}				
			} else {
				logger.error(() -> "findOne ->" + DbUtils.index(clazz) + " on error -> " + x.cause());
			}
			
			this.secureThenCall(then, result);			
		});
		return then;
	}
	
	@Override
	public <T,R> MongoCallBack<List<R>> findInternListFromObject(Class<T> clazz,Class<R> clazzR, JsonObject query, JsonObject fields) {
		MongoCallBack<List<R>> then = new MongoCallBack<>();
		
		mongoClient.findOne(DbUtils.index(clazz), query, fields, x -> {
			
			logger.debug(() -> "findInternListFromObject -> " + (x.result() != null));
			
			JsonArray array = x.result().getJsonArray("ticketHistory", new JsonArray());
			
			List<R> result = new ArrayList<>();
			if (array != null && array.getList().size()>0) {
				for (Object o : array){
					if (o instanceof JsonObject){
						
						result.add(Json.decodeValue(Json.encodePrettily(o), clazzR));
					}
				}
			}
			
			this.secureThenCall(then, result);			
		});
		
		return then;
	}
	
	
	@Override
	public <T> MongoCallBack<Boolean> insert(T entity){
		MongoCallBack<Boolean> then = new MongoCallBack<>();
		String json = Json.encode(entity);
		JsonObject jsonEntity = new JsonObject(json);
		mongoClient.insert(DbUtils.index(entity.getClass()), jsonEntity, x -> {
			//logger.debug(() -> "MongoService.insert -> " + x.succeeded());
			then.finish(x.succeeded());
		});	
		return then;
		
	}
	
	@Override
	public <T> MongoCallBack<List<T>> findAll(Class<T> clazz) {
		
		return findAll(clazz, new JsonObject());
	}
	
	@Override
	public <T> MongoCallBack<List<T>> findAll(Class<T> clazz, JsonObject query) {
		return findAll( clazz, query, null);
	}
	
	@Override
	public <T> MongoCallBack<List<T>> findAll(Class<T> clazz, JsonObject query,Sort sort) {
		logger.debug(() -> "MongoService.findAll");
		
		MongoCallBack<List<T>> then = new MongoCallBack<>();
		FindOptions options = new FindOptions();
		
		Object sortJson = query.getValue("sort");
		if (sortJson != null) {
			if (sort == null) sort = Sort.ASC;
			
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
				this.secureThenCall(then, liste);				
			} else {
				logger.error(() -> "ERROR : " + x.cause());
				this.secureThenCall(then, new ArrayList<>());
			}
			
		});
		return then;
	}
	
	@Override
	public <T> MongoCallBack<Integer> getNextSequence(Class<T> clazz){
		MongoCallBack<Integer> then = new MongoCallBack<>();
		mongoClient.update("counters", 
				new JsonObject().put("_id", DbUtils.index(clazz)), 
				new JsonObject().put("$inc", new JsonObject().put("seq", 1)), s -> {
					if (s.succeeded()){						
						mongoClient.findOne("counters", new JsonObject().put("_id", DbUtils.index(clazz)), null, i -> {
							this.secureThenCall(then, i.result().getInteger("seq"));							
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
	
	@Override
	public <T> MongoCallBack<List<T>> fullSearchCommand(Class<T> index, JsonObject command){
		
		MongoCallBack<List<T>> callback = new MongoCallBack<>();
		command.put("find", DbUtils.index(index));
		mongoClient.runCommand("find", command, x -> {
			List<T> liste = new ArrayList<>();
			if (x.succeeded()){
				System.out.println("fullSearchCommand -> " + x.result().encodePrettily());
				
				JsonArray array = x.result().getJsonObject("cursor").getJsonArray("firstBatch");
				for (Object itemObject : array){
					JsonObject item = (JsonObject)itemObject;
					T result = Json.decodeValue(item.encodePrettily(), index);
					liste.add(result);
				}
				this.secureThenCall(callback, liste);
			} else {
				System.out.println("fullSearchCommand failled -> " + x.cause());
			}
			
		});
		
		
		return callback;
	}
	
	@Override
	public void createIndex(Consumer<Boolean> callback){
		//{ dropIndexes: "collection", index: "*" }
		
		mongoClient.runCommand("listIndexes", new JsonObject().put("listIndexes", DbUtils.index(Ticket.class)), indexs -> {
			System.out.println("indexes ->" + Json.encodePrettily(indexs.result()));
		
		
		
		mongoClient.runCommand("dropIndexes", new JsonObject()
				.put("dropIndexes",  DbUtils.index(Ticket.class))
				.put("index", "*"), r -> {
					
					mongoClient.runCommand("createIndexes"	, new JsonObject()
							.put("createIndexes", DbUtils.index(Ticket.class))
							.put("indexes", 
									new JsonArray()
									.add(new JsonObject()
											.put("key",new JsonObject()			
													.put("reference", "text")
													.put("summary", "text")
													.put("description", "text")													
													.put("owner.login", 1)		
													.put("application.name", 1)
												)							
											.put("weights", new JsonObject()
													.put("reference", 10)
													.put("summary", 2)
													.put("description", 1))
												
											.put("name", "ticket_fields_index")
											
											
										)
									
								), x -> {									
								System.out.println("createIndex ok -> " + Json.encode(x.result()));
								
								mongoClient.runCommand("listIndexes", new JsonObject().put("listIndexes", DbUtils.index(Ticket.class)),
										secondIndexes -> System.out.println("second indexes -> " + Json.encodePrettily(secondIndexes)));
								
								
								if (x.failed()){
									System.out.println("createIndex KO -> " + x.cause());
								}
								callback.accept(x.succeeded());
								
							});
					
				});
		
		});
	}
	
	
	
	
}
