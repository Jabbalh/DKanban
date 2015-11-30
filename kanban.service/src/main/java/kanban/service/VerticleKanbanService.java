package kanban.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import kanban.bus.constants.EventBusNames;
import kanban.entity.db.Ticket;
import kanban.entity.ui.CardTicket;
import kanban.entity.ui.ComplexColumn;
import kanban.entity.ui.SimpleColumn;
import kanban.entity.ui.Zone;
import kanban.service.contract.IMongoService;
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
		UiUtils.headersWithWidth().forEach(x ->	result.add(new SimpleColumn(x.getZoneTicket().getCodeZone(),x.getWidth())));		
		m.reply(Json.encodePrettily(result));
	}
		
	
	/**
	 * Renvois le détail d'un couloir de kanban pour un utilisateur
	 * @param message
	 */
	private void handleByUser(Message<String> message) {			
		
		JsonObject query = new JsonObject()
				.put("$and", new JsonArray().add(new JsonObject().put("owner.login", message.body()))
				.add(new JsonObject().put("archive", false)));
		System.out.println("handleByUser -> " + query.encodePrettily());
		mongoService.findAll(Ticket.class,query , x -> {			
			String login = message.body();
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
			});
			message.reply(Json.encodePrettily(zone));
		});
		
	}
	
	
}
