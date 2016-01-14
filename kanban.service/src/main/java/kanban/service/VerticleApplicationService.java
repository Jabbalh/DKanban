package kanban.service;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import kanban.bus.constants.EventBusNames;
import kanban.db.entity.*;
import kanban.entity.session.ApplicationData;
import kanban.service.contract.ICryptoService;
import kanban.service.contract.IMongoService;
import kanban.service.impl.CryptoService;
import kanban.service.utils.DbUtils;
import kanban.ui.entity.UserMdp;
import kanban.utils.callback.Async;

public class VerticleApplicationService extends AbstractVerticle {

	@Inject
	private IMongoService mongoService;

	@Inject
	private ICryptoService cryptoService;
	
	@Override
	public void start(){
		
		vertx.eventBus().consumer(EventBusNames.KANBAN_FULL, (Message<String> x) -> {
			Async.When(() -> mongoService.findAll(KanbanParameter.class)).doThat(r -> x.reply(Json.encode(r)));
		});
		
		// List des paramètres d'admin transformé pour un ticket
		vertx.eventBus().consumer(EventBusNames.APPLICATION_LIST, (Message<String> x) 	-> listForTicketParameter(x, ApplicationParameter.class, 0, t -> new ParamTuple(t))); // appList(x);
		vertx.eventBus().consumer(EventBusNames.STATE_LIST, (Message<String> x) 		-> listForTicketParameter(x, StatutParameter.class, 0, new JsonObject().put("sort", "libelle"), t -> new ParamColorTuple(t))); //stateList(x));
		vertx.eventBus().consumer(EventBusNames.ZONE_LIST, (Message<String> x) 			-> listForTicketParameter(x, ZoneParameter.class, 1, new JsonObject().put("sort", "order"), t -> new ParamTuple(t))); //zoneList(x));
		vertx.eventBus().consumer(EventBusNames.PRIORITY_LIST, (Message<String> x) 		-> listForTicketParameter(x, PriorityParameter.class, 0, new JsonObject().put("sort", "libelle"), t -> new ParamColorTuple(t))); //priorityList(x));
		
		// gestion du titre
		vertx.eventBus().consumer(EventBusNames.GLOBAL_TITLE_GET, this::globalTitleGet);
		vertx.eventBus().consumer(EventBusNames.GLOBAL_TITLE_SET, this::globalTitleSet);
		
		
		// Liste des paramètres des écrans d'admin
		vertx.eventBus().consumer(EventBusNames.ADMIN_APP_LIST, (Message<String> x) 	-> fullAdminList(x, ApplicationParameter.class));
		vertx.eventBus().consumer(EventBusNames.ADMIN_STATUT_LIST, (Message<String> x) 	-> fullAdminList(x, StatutParameter.class));		
		vertx.eventBus().consumer(EventBusNames.ADMIN_ZONE_LIST, (Message<String> x) 	-> fullAdminList(x,ZoneParameter.class));
		vertx.eventBus().consumer(EventBusNames.ADMIN_PRORITY_LIST, (Message<String> x) -> fullAdminList(x,PriorityParameter.class));
		
		
		// Sauvegarde des paramètres
		vertx.eventBus().consumer(EventBusNames.PRIORITY_SAVE, (Message<JsonObject> x) 	-> saveAndUpdateTicket(x, PriorityParameter.class, "priority", t -> new ParamColorTuple( t), (t,e) -> t.setPriority(e), () -> ApplicationData.get().getPriority()) ); 
		vertx.eventBus().consumer(EventBusNames.STATE_SAVE, (Message<JsonObject> x) 	-> saveAndUpdateTicket(x, StatutParameter.class, "statut", t -> new ParamColorTuple(t), (t,e) -> t.setStatut(e), () -> ApplicationData.get().getStatut()) );		
		vertx.eventBus().consumer(EventBusNames.ZONE_SAVE, (Message<JsonObject> x) 		-> saveAndUpdateTicket(x, ZoneParameter.class, "zone", t -> new ParamTuple(t), (t,e) -> t.setZone(e), () -> ApplicationData.get().getZones()) );		
		vertx.eventBus().consumer(EventBusNames.APPLICATION_SAVE,(Message<JsonObject> x)-> saveAndUpdateTicket(x, ApplicationParameter.class, "application", t -> new ParamTuple(t), (t,e) -> t.setApplication(e), () -> ApplicationData.get().getApplications()) );
		
		// Insertion des paramètres
		vertx.eventBus().consumer(EventBusNames.STATE_INSERT, (Message<JsonObject> x) 	-> insert(x, StatutParameter.class));		
		vertx.eventBus().consumer(EventBusNames.PRIORITY_INSERT, (Message<JsonObject> x)-> insert(x, PriorityParameter.class));
	
		// Suppression d'un paramètre
		vertx.eventBus().consumer(EventBusNames.ADMIN_DELETE, this::deleteAdminEntity);

		// modification d'un mot de passe utilisateur
		vertx.eventBus().consumer(EventBusNames.ADMIN_USER_UP_PASSWORD, this::userUpdatePassword);
	}

	private void userUpdatePassword(Message<JsonObject> message) {
		UserMdp userMdp = Json.decodeValue(message.body().getJsonObject("data").encode(),UserMdp.class);

		userMdp.setNewPassword(cryptoService.genHash256(userMdp.getNewPassword()));
		userMdp.setOldPassword(cryptoService.genHash256(userMdp.getOldPassword()));

		Async.When(() -> mongoService.findOne(User.class,new JsonObject().put("login",userMdp.getLogin())))
				.Rule(user -> (user.getPassword().equals(userMdp.getOldPassword())))
				.Otherwise(user -> message.reply(Json.encode("L'ancien mot de passe ne correspond pas")))
				.doThat(user -> {
					Async.When(() -> mongoService.update(
										DbUtils.index(User.class),
										new JsonObject().put("login",userMdp.getLogin()),
										new JsonObject().put("$set",new JsonObject().put("password",userMdp.getNewPassword()))))
					.doThat(x -> message.reply(Json.encode(x)));
				});

	}


	/**
	 * Renvois la liste complète d'un paramètre d'administration
	 * @param message
	 * @param clazz
	 */
	private <T> void fullAdminList(Message<String> message,Class<T> clazz){
		Async.When(()-> mongoService.findAll(clazz, kanbanQuery()))
		.doThat(x -> message.reply(Json.encode(x)));
	}
	
	/**
	 * Suppression d'un paramètre d'administration
	 * @param message
	 */
	private void deleteAdminEntity(Message<JsonObject> message){				
		try {
			Class<?> c = Class.forName(message.body().getString("clazz"));
			Async.When(()-> mongoService.deleteEntity(c, message.body().getString("id")))
			.doThat(x -> message.reply(Json.encode(x)));	
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Insertion d'un paramètre d'administration
	 * @param message
	 * @param clazz
	 */
	private <T extends AbstractParameter> void insert(Message<JsonObject> message, Class<T> clazz){
		T entity = Json.decodeValue(message.body().encode(), clazz);
		entity.setKanbanParameter(KanbanParameter.EXPOSED_ID);
		
		Async.When(() -> mongoService.insert(entity))
		.doThat(c -> message.reply(Json.encode(entity)));
	}
	
	
	/**
	 * Sauvegarde d'un paramètre d'admin et mise à jour des tickets qui y sont liés
	 * @param message 		: Objet de lecture d'entrée et de réponse 
	 * @param clazz			: Class de l'objet d'admin
	 * @param propertyName	: Nom de la propriété dans le ticket
	 * @param transform		: Fonction de transformation (Objet d'admin -> Objet du ticket)
	 * @param setToTicket	: BiFonction settant l'objet tranformé dans le ticket
	 * @param getList		: Fonction renvoyant la liste de l'objet d'admin issu de la classe static (à supprimer un jour)
	 */
	private <T extends AbstractParameter,R> void saveAndUpdateTicket(
			Message<JsonObject> message, 
			Class<T> clazz,
			String propertyName, 
			Function<T, R> transform,
			BiConsumer<Ticket, R> setToTicket,			
			Supplier<List<T>> getList){
		
		// On decode la classe par rapport au Json
		T entity = Json.decodeValue(message.body().encode(), clazz);
		// On applique la transformation pour convertir en objet consommable par le ticket
		R paramTuple = transform.apply(entity);
		
		// Mise à jour de tous les tickets
		updateAllTicket(propertyName, setToTicket, entity, paramTuple);
		
		// On met à jour la list statique @TODO changer ce trucs moche un jour		
		List<T> liste = getList.get();
		T toUp = liste.stream().filter(x -> x.getCode().equals(entity.getCode())).findFirst().get();
		toUp.clone(entity);
		
		// On met à jour l'entité
		Async.When(()-> mongoService.update(entity)).doThat(r -> message.reply(Json.encode(r)));
		
	}

	/**
	 * Mise à jour d'un paramètre d'admin pour tous les tickets
	 * @param propertyName
	 * @param setToTicket
	 * @param entity
	 * @param paramTuple
	 */
	private <R, T extends AbstractParameter> void updateAllTicket(String propertyName,BiConsumer<Ticket, R> setToTicket, T entity, R paramTuple) {
		
		Async.When(()-> mongoService.findAll(Ticket.class, new JsonObject().put(propertyName + ".code",entity.getCode())) )
		.doThat(tickets -> {
			// On met à jour tous les tickets
			for (Ticket item : tickets){
				setToTicket.accept(item,paramTuple);
				
				Async.When(()-> mongoService.update(
						DbUtils.index(Ticket.class), 
						new JsonObject().put("_id",item.get_id()), 
						new JsonObject().put("$set", new JsonObject().put(propertyName, new JsonObject(Json.encode(paramTuple))))))
				.doThat(b -> {					
					vertx.eventBus().publish(EventBusNames.UPDATE_CARD, Json.encodePrettily(item));
				});
			}						
		});
	}
	
	
	private JsonObject kanbanQuery() { return new JsonObject().put("kanbanParameter", KanbanParameter.EXPOSED_ID);  }
	
	private <T,R> void listForTicketParameter(Message<String> message, Class<T> clazz, int skip, Function<T, R> map) {
		
		listForTicketParameter(message, clazz, 0,null, map);
	}
	
	private <T,R> void listForTicketParameter(Message<String> message, Class<T> clazz, int skip, JsonObject order, Function<T, R> map) {
		
		JsonObject query = kanbanQuery();
		if (order != null){
			query.mergeIn(order);
		}
		
		Async.When(()-> mongoService.findAll(clazz, query))
		.doThat(r -> message.reply(Json.encode(r.stream().skip(skip).map(map).collect(Collectors.toList()))));
	}
	
	
	/**
	 * Renvois le titre de l'application
	 * @param message
	 */
	private void globalTitleGet(Message<JsonObject> message){		
		//extractInternalField(String.class, "title").doThat(r -> message.reply(Json.encode(r)));
		message.reply(Json.encode("CDC"));
	}
	
	/**
	 * Met à jour le titre de l'application
	 * @param message
	 */
	private void globalTitleSet(Message<JsonObject> message){		
		
		/*
		Async.When(() -> mongoService.update(
				DbUtils.index(KanbanParameter.class), 
				new JsonObject().put("_id", KanbanParameter.EXPOSED_ID), new JsonObject().put("$set", new JsonObject().put("title", message.body().getString("title")))))
		.doThat(x -> message.reply(Json.encode(x)));
		*/
		Json.encode("true");
	}
	
	
	
}
