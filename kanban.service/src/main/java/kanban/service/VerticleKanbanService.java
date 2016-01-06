package kanban.service;

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
				
				
		vertx.eventBus().consumer(EventBusNames.KANBAN_HEADER_LIST, (Message<String> m) -> handlerHeaderList(m) );		
		vertx.eventBus().consumer(EventBusNames.KANBAN_BY_USER, 	(Message<String> m) -> handleByUser(m));
		
		
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
			/*
			
			
			Zone<CardTicket> zone = new Zone<CardTicket>();
			zone.setFirst(new SimpleColumn(UUID.randomUUID().toString(), login, 1));
			// Le 1ère colonne est la colonne User, pas de ticket sur celle-ci, donc on la skip (elle a jouté juste au dessus)
			UiUtils.headersWithWidth().skip(1).forEach(h -> {		
				String header = h.getZoneTicket().getCodeZone();
				ComplexColumn<CardTicket> column = new ComplexColumn<>(UiUtils.otherZoneColumnId(login, header) ,h.getWidth());
				// Filtre des tickets souhaité
				Stream<Ticket> streamTicket = x.stream().filter(t -> t.getZoneTicket().getCodeZone().equals(header));
				
				streamTicket.forEach(t -> column.addCard(CardTicket.fromTicket(t)));						
				zone.addOther(column);
			});*/
			//columns.sort((a,b) -> a.getZone().getOrder().compareTo(b.getZone().getOrder()));
			message.reply(Json.encodePrettily(kanban));
		});
		
	}
	
	
}
