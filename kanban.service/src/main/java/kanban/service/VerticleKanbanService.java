package kanban.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import kanban.bus.constants.EventBusNames;
import kanban.db.entity.PriorityParameter;
import kanban.db.entity.Ticket;
import kanban.db.entity.ZoneParameter;
import kanban.entity.session.ApplicationData;
import kanban.service.contract.IMongoService;
import kanban.ui.entity.HeaderColumn;
import kanban.ui.entity.Kanban;
import kanban.ui.entity.KanbanColumn;
import kanban.ui.entity.KanbanFirstColumn;
import kanban.utils.callback.Async;

public class VerticleKanbanService extends AbstractVerticle {
	
	//private static final Logger logger = LoggerFactory.getLogger(VerticleKanbanService.class);
	
	@Inject
	private IMongoService mongoService;
	
	@Override
	public void start() {
				
				
		vertx.eventBus().consumer(EventBusNames.KANBAN_HEADER_LIST, 			this::handlerHeaderList );
		vertx.eventBus().consumer(EventBusNames.KANBAN_HEADER_LIST_PRIORITY, 	this::handlerHeaderPriorityList );

		vertx.eventBus().consumer(EventBusNames.KANBAN_BY_USER, 				this::handleByUser);
		vertx.eventBus().consumer(EventBusNames.KANBAN_BY_USER_FOR_PRIORITY, 	this::handleByUserForPriority);
		
		
	}
	
	
	
	/**
	 * Renvois la liste des headers
	 * @param m
	 */
	private void handlerHeaderList(Message<String> m){				 		
		 List<HeaderColumn> result = ApplicationData.get().getZones()
			.stream()
			.sorted((x,y)-> x.getOrder().compareTo(y.getOrder()))
			.map(x -> new HeaderColumn(x.getWidth(), x.getLibelle(), x.getOrder()))
			.collect(Collectors.toList());
		 m.reply(Json.encodePrettily(result));		
	}

	private void handlerHeaderPriorityList(Message<String> m){

		Async.When(() -> mongoService.findAll(PriorityParameter.class,new JsonObject().put("sort","code")))
				.doThat(p -> {
					List<HeaderColumn> result = new ArrayList<>();
					int i=1;
					result.add(new HeaderColumn(1,"",i++, "root"));
					for(PriorityParameter param : p){
						result.add(new HeaderColumn(2,param.getLibelle(),i++, param.getCode()));
					}
					m.reply(Json.encode(result));
				});
	}

	private void handleByUserForPriority(Message<String> message) {

		JsonObject query = new JsonObject()
				.put("$and", new JsonArray().add(new JsonObject().put("owner.code", message.body()))
						.add(new JsonObject().put("archive", false)));
		Async.When(() -> mongoService.findAll(Ticket.class,query))
				.doThat(x -> {
					String login = message.body();


					Async.When(() -> mongoService.findAll(PriorityParameter.class,new JsonObject().put("sort","code")))
							.doThat(p -> {
							List<HeaderColumn> zones = new ArrayList<>();
							int index=1;
							zones.add(new HeaderColumn(1,"",0, "root"));
							for(PriorityParameter param : p){
								zones.add(new HeaderColumn(2,param.getLibelle(),index++, param.getCode()));
							}

						//List<ZoneParameter> zones = ApplicationData.get().getZones();
						zones.sort((a,b) -> a.getOrder().compareTo(b.getOrder()));

						Kanban kanban = new Kanban();
						kanban.setFirstColumn(new KanbanFirstColumn());
						kanban.getFirstColumn().setId(login+"$"+zones.get(0).getCode());
						kanban.getFirstColumn().setLibelle(login);

						List<KanbanColumn> columns = new LinkedList<>();
						for (int i=1;i<zones.size();i++){
							HeaderColumn zone = zones.get(i);
							KanbanColumn column = new KanbanColumn();
							column.setId(login+"$"+ zone.getCode());
							column.setZone(new ZoneParameter(z -> {
								z.setDroppableArchive(false);
								z.setDroppableTicket(false);
								z.setOrder(zone.getOrder());
								z.setWidth(zone.getWidth());
								z.setCode(zone.getCode());
								z.setLibelle(zone.getLibelle());
							}));
							column.setTickets(x.stream().filter(t -> t.getPriority().getCode().equals(zone.getCode())).collect(Collectors.toList()));
							columns.add(column);
						}
						kanban.setColumns(columns);

						message.reply(Json.encodePrettily(kanban));
					});


				});
	}
	
	/**
	 * Renvois le détail d'un couloir de kanban pour un utilisateur
	 * @param message
	 */
	private void handleByUser(Message<String> message) {			
		
		JsonObject query = new JsonObject()
				.put("$and", new JsonArray().add(new JsonObject().put("owner.code", message.body()))
				.add(new JsonObject().put("archive", false)));		
		Async.When(() -> mongoService.findAll(Ticket.class,query))
		.doThat(x -> {			
			String login = message.body();
			
			
			List<ZoneParameter> zones = ApplicationData.get().getZones();
			zones.sort((a,b) -> a.getOrder().compareTo(b.getOrder()));
			
			Kanban kanban = new Kanban();
			kanban.setFirstColumn(new KanbanFirstColumn());
			kanban.getFirstColumn().setId(login+"$"+zones.get(0).getCode());
			kanban.getFirstColumn().setLibelle(login);
			
			List<KanbanColumn> columns = new LinkedList<>();
			for (int i=1;i<zones.size();i++){
				ZoneParameter zone = zones.get(i);
				KanbanColumn column = new KanbanColumn();
				column.setId(login+"$"+ zone.getCode());
				column.setZone(zone);
				column.setTickets(x.stream().filter(t -> t.getZone().getCode().equals(zone.getCode())).collect(Collectors.toList()));
				columns.add(column);
			}
			kanban.setColumns(columns);

			message.reply(Json.encodePrettily(kanban));
		});
		
	}
	
	
}
