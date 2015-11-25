package kanban.service.init;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import kanban.entity.db.Application;
import kanban.entity.db.StateTicket;
import kanban.entity.db.Ticket;
import kanban.entity.db.User;
import kanban.entity.db.ZoneTicket;
import kanban.entity.db.parameter.ApplicationData;
import kanban.entity.db.parameter.ApplicationParameter;
import kanban.entity.db.parameter.ZoneApp;
import kanban.service.contract.IMongoService;
import kanban.service.utils.DbUtils;

public class ApplicationService extends AbstractVerticle {

	public static String INIT_FIRST_APP = "INIT_FIRST_APP";
	public static String INIT_DATA_APP = "INIT_DATA_APP";
	public static String INIT_APPLICATION = "INIT_APPLICATION";
	
	@Inject
	private IMongoService mongoService;
	
	@Override
	public void start() {
		
		vertx.eventBus().consumer(INIT_FIRST_APP, 	x -> this.initParameter(x) );
		vertx.eventBus().consumer(INIT_DATA_APP, 	x -> this.initData(x));
		vertx.eventBus().consumer(INIT_APPLICATION,	x -> this.initApplication(x));
		
		System.out.println("ApplicationService .. run ...");
		
	}
	
	/**
	 * Initialisation de l'application
	 */
	private void initApplication(Message<Object> message) {
		System.out.println("INIT_APPLICATION -> initApplication");
		mongoService.findAll(DbUtils.index(ApplicationParameter.class), ApplicationParameter.class, x -> {
			for (ApplicationParameter item : x){
				System.out.println(Json.encodePrettily(item));
			}
			ApplicationData.set(x.get(0));	
			message.reply("OK");
		});
	}
	
	/**
	 * Initialisation des paramètres (mode dev ou first)
	 */
	private void initParameter(Message<Object> message){	
		System.out.println("INIT_FIRST_APP -> initParameter");
		List<Application> applications = new ArrayList<>();
		applications.add(new Application("DEI PART", "Banque à distance pour particulier"));
		applications.add(new Application("DEI PRO", "Banque à distance pour professionel"));
		applications.add(new Application("DEA", "Site d'assurance viture/habitation"));
		applications.add(new Application("NOSA WEB", "Site d'assurance vie"));
		applications.add(new Application("SVI", "Serveur vocal"));
				
		ApplicationData.get().setApplications(applications);
				
		
		List<ZoneApp> states = new ArrayList<>();
		states.add(new ZoneApp(new ZoneTicket("Utilisateur"),0,1));
		states.add(new ZoneApp(new ZoneTicket("BackLog"),1,2));
		states.add(new ZoneApp(new ZoneTicket("Analyse"),2,2));
		states.add(new ZoneApp(new ZoneTicket("Dev"),3,2));
		states.add(new ZoneApp(new ZoneTicket("VFO"),4,1));
		states.add(new ZoneApp(new ZoneTicket("UTI"),5,1));
		states.add(new ZoneApp(new ZoneTicket("QPA"),6,1));
		states.add(new ZoneApp(new ZoneTicket("PROD"),7,2));
		ApplicationData.get().setZones(states);
		
		if (!ApplicationData.get().isInit()){
			mongoService.delete(DbUtils.index(ApplicationParameter.class), () -> {
				mongoService.insert(DbUtils.index(ApplicationParameter.class), ApplicationData.get(), x -> {				
					if (x.succeeded()) {
						System.out.println("Application initialized");
						ApplicationData.get().setInit(true);
						message.reply("OK");
					} else {
						System.out.println("Application NOT initialized -> " + x.cause());
						message.reply("NOK");
					}
				});
			});
		}
		
		
		List<ZoneTicket> statesTicket = new ArrayList<>();
		
		ApplicationData.get().getZones().forEach(x -> statesTicket.add(x.getZoneTicket()));
		
		deleteAndInit(mongoService,DbUtils.index(Application.class), applications, a -> "application " + a.getName());
		deleteAndInit(mongoService,DbUtils.index(StateTicket.class), statesTicket, s -> "state "+s.getCodeZone());
		
		message.reply("OK");
	}
	
	
	/***
	 * Initialisation des données (mode Dev)
	 */
	private void initData(Message<Object> message) {
		System.out.println("INIT_DATA_APP -> initData");
		List<Application> applications = ApplicationData.get().getApplications();
		List<ZoneTicket> states = new ArrayList<>();
		
		ApplicationData.get().getZones().forEach(x -> states.add(x.getZoneTicket()));
				
		List<User> users = new LinkedList<>();
		users.add(new User("user1", "user1", "User 1", "User 1"));
		users.add(new User("user2", "user2", "User 2", "User 2"));
		
		
				
		List<Ticket> tickets = new LinkedList<>();
		tickets.add(new Ticket("ARS01", "Test ARS1", "Desc ARS1", applications.get(0), states.get(1),users.get(0),"14445"));
		tickets.add(new Ticket("ARS02", "Test ARS2", "Desc ARS2", applications.get(0), states.get(1),users.get(0),"14445"));
		tickets.add(new Ticket("ARS03", "Test ARS3", "Desc ARS3", applications.get(1), states.get(2),users.get(0),"14445"));
		tickets.add(new Ticket("ARS04", "Test ARS4", "Desc ARS4", applications.get(2), states.get(1),users.get(1),"14445"));
		tickets.add(new Ticket("ARS05", "Test ARS5", "Desc ARS5", applications.get(2), states.get(1),users.get(1),"14445"));
		tickets.add(new Ticket("ARS06", "Test ARS6", "Desc ARS6", applications.get(3), states.get(3),users.get(1),"14445"));
		
		
		
		deleteAndInit(mongoService,DbUtils.index(User.class), users, x -> "user " + x.getLogin());
		
							
		deleteAndInit(mongoService, DbUtils.index(Ticket.class), tickets, t -> "ticket " + t.getReference());
		
		message.reply("OK");
	}
	
	

	private <T> void deleteAndInit(IMongoService mongoService,String index, List<T> liste, Function<T, String> message) {
		mongoService.delete(index, () -> {			
			for (T u : liste) {
				System.out.println("Before insert of " + message.apply(u));
				mongoService.insert(index, u, genericCallback(message.apply(u)));
			}
			
		});
	}
	
	
	
	
	private Handler<AsyncResult<String>> genericCallback(String message) {
		return x -> {
			if (x.succeeded()) {
				System.out.println("insertion SUCCEEDED for " +message );
			} else {
				
				System.out.println("insertion FAILED for " +message  );
				System.out.println(x.cause());
			}
		};
	}
	
	
}
