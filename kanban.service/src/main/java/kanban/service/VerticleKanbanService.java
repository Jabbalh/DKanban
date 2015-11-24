package kanban.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import kanban.bus.constants.EventBusNames;
import kanban.entity.db.Ticket;
import kanban.entity.ui.CardTicket;
import kanban.entity.ui.ComplexColumn;
import kanban.entity.ui.SimpleColumn;
import kanban.entity.ui.Zone;
import kanban.service.contract.IMongoService;
import kanban.service.utils.DbUtils;
import kanban.service.utils.UiUtils;

public class VerticleKanbanService extends AbstractVerticle {
	
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
		List<SimpleColumn> result = new ArrayList<>();
		UiUtils.headersWithWidth().forEach(x ->	result.add(new SimpleColumn(x.getStateTicket().getName(),x.getWidth())));		
		m.reply(Json.encodePrettily(result));
	}
		
	
	/**
	 * Renvois le détail d'un couloir de kanban pour un utilisateur
	 * @param message
	 */
	private void handleByUser(Message<String> message) {			
		
		mongoService.findAll(DbUtils.index(Ticket.class), Ticket.class,new JsonObject().put("owner.login", message.body()) , x -> {			
			String login = message.body();
			Zone zone = new Zone();
			zone.setFirst(new SimpleColumn(UUID.randomUUID().toString(), login, 1));
			// Le 1ère colonne est la colonne User, pas de ticket sur celle-ci, donc on la skip (elle a jouté juste au dessus)
			UiUtils.headersWithWidth().skip(1).forEach(h -> {		
				String header = h.getStateTicket().getName();
				ComplexColumn<CardTicket> column = new ComplexColumn<>(UiUtils.otherZoneColumnId(login, header) ,h.getWidth());
				// Filtre des tickets souhaité
				Stream<Ticket> streamTicket = x.stream().filter(t -> t.getStateTicket().getName().equals(header));
				
				streamTicket.forEach(t -> column.addCard(new CardTicket(
															t.get_id(), 
															t.getReference(), 
															t.getApplication().getName(),
															t.getSummary())));
				zone.addOther(column);
			});
			message.reply(Json.encodePrettily(zone));
		});
		
	}
	
	
}
