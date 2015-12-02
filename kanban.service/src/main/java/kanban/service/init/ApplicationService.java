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
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import kanban.entity.db.Application;
import kanban.entity.db.StateTicket;
import kanban.entity.db.Ticket;
import kanban.entity.db.User;
import kanban.entity.db.ZoneTicket;
import kanban.entity.db.parameter.ApplicationData;
import kanban.entity.db.parameter.ApplicationParameter;
import kanban.entity.db.parameter.ZoneApp;
import kanban.service.contract.ICryptoService;
import kanban.service.contract.IMongoService;
import kanban.service.utils.DbUtils;

public class ApplicationService extends AbstractVerticle {

	private final static Logger logger = LoggerFactory.getLogger(ApplicationService.class);
	
	public static String INIT_FIRST_APP = "INIT_FIRST_APP";
	public static String INIT_DATA_APP = "INIT_DATA_APP";
	public static String INIT_APPLICATION = "INIT_APPLICATION";
	
	@Inject
	private IMongoService mongoService;
	
	@Inject
	private ICryptoService cryptoService;
	
	@Override
	public void start() {
		
		vertx.eventBus().consumer(INIT_FIRST_APP, 	x -> this.initParameter(x) );
		vertx.eventBus().consumer(INIT_DATA_APP, 	x -> this.initData(x));
		vertx.eventBus().consumer(INIT_APPLICATION,	x -> this.initApplication(x));
		
		logger.debug("ApplicationService .. run ...");
		
	}
	
	/**
	 * Initialisation de l'application
	 */
	private void initApplication(Message<Object> message) {
		logger.debug("INIT_APPLICATION -> initApplication");
		mongoService.findAll(ApplicationParameter.class, x -> {
			
			ApplicationData.set(x.get(0));	
			message.reply("OK");
			
			mongoService.findAll(User.class, users -> {
				for (User u : users) {
					u.setPassword(cryptoService.genHash256(u.getLogin()));
					mongoService.update(u, b -> System.out.println("User " + u.getLogin() + " update " + b));					
				}
			});
			
		});
	}
	
	/**
	 * Initialisation des paramètres (mode dev ou first)
	 */
	private void initParameter(Message<Object> message){	
		logger.debug("INIT_FIRST_APP -> initParameter");
		List<Application> applications = new ArrayList<>();
		applications.add(new Application("DEI PART", "Banque à distance pour particulier"));
		applications.add(new Application("DEI PRO", "Banque à distance pour professionel"));
		applications.add(new Application("DEA", "Site d'assurance viture/habitation"));
		applications.add(new Application("NOSA WEB", "Site d'assurance vie"));
		applications.add(new Application("SVI", "Serveur vocal"));
				
		ApplicationData.get().setApplications(applications);
				
		
		List<ZoneApp> zoneApps = new ArrayList<>();
		zoneApps.add(new ZoneApp(new ZoneTicket("U"),0,1));
		zoneApps.add(new ZoneApp(new ZoneTicket("BackLog"),1,2));
		zoneApps.add(new ZoneApp(new ZoneTicket("Analyse"),2,2));
		zoneApps.add(new ZoneApp(new ZoneTicket("Dev"),3,2));
		zoneApps.add(new ZoneApp(new ZoneTicket("VFO"),4,2));
		zoneApps.add(new ZoneApp(new ZoneTicket("UTI"),5,2));
		zoneApps.add(new ZoneApp(new ZoneTicket("QPA"),6,2));
		zoneApps.add(new ZoneApp(new ZoneTicket("PROD"),7,2));
		ApplicationData.get().setZones(zoneApps);
		
		
		List<StateTicket> statesTicket = new ArrayList<>();
		statesTicket.add(new StateTicket("IN_PROGRESS", "En cours"));
		statesTicket.add(new StateTicket("STAND_BY", "En attente"));
		ApplicationData.get().setStatesTicket(statesTicket);
		
		
		if (!ApplicationData.get().isInit()){
			mongoService.delete(DbUtils.index(ApplicationParameter.class), () -> {
				mongoService.insert(ApplicationData.get(), x -> {				
					if (x.succeeded()) {
						logger.debug("Application initialized");
						ApplicationData.get().setInit(true);
						message.reply("OK");
					} else {
						logger.error("Application NOT initialized -> " + x.cause());
						message.reply("NOK");
					}
				});
			});
		}
		
		
		List<ZoneTicket> zoneTicket = new ArrayList<>();
		
		ApplicationData.get().getZones().forEach(x -> zoneTicket.add(x.getZoneTicket()));
		
		deleteAndInit(mongoService,DbUtils.index(Application.class), applications, a -> "application " + a.getName());
		deleteAndInit(mongoService,DbUtils.index(ZoneTicket.class), zoneTicket, s -> "zone "+s.getCodeZone());
		deleteAndInit(mongoService,DbUtils.index(StateTicket.class), statesTicket, s -> "state "+s.getCode());
		
		
		mongoService.reinitCounters();
		
		
		message.reply("OK");
	}
	
	
	/***
	 * Initialisation des données (mode Dev)
	 */
	private void initData(Message<Object> message) {
		logger.debug("INIT_DATA_APP -> initData");
		List<Application> applications = ApplicationData.get().getApplications();
		List<ZoneTicket> zones = new ArrayList<>();
		
		ApplicationData.get().getZones().forEach(x -> zones.add(x.getZoneTicket()));
				
		List<User> users = new LinkedList<>();
		users.add(new User("user1", "user1", "User 1", "User 1"));
		users.add(new User("user2", "user2", "User 2", "User 2"));
		
		List<StateTicket> states = ApplicationData.get().getStatesTicket();
				
		List<Ticket> tickets = new LinkedList<>();
		tickets.add(new Ticket(0,"ARS01", "Test ARS1", "Desc ARS1", applications.get(0), zones.get(1),users.get(0),"14445", states.get(0)));
		tickets.add(new Ticket(1,"ARS02", "Test ARS2", "Desc ARS2", applications.get(0), zones.get(1),users.get(0),"14445", states.get(0)));
		tickets.add(new Ticket(2,"ARS03", "Test ARS3", "Desc ARS3", applications.get(1), zones.get(2),users.get(0),"14445", states.get(0)));
		tickets.add(new Ticket(3,"ARS04", "Test ARS4", "Desc ARS4", applications.get(2), zones.get(1),users.get(1),"14445", states.get(0)));
		tickets.add(new Ticket(4,"ARS05", "Test ARS5", "Desc ARS5", applications.get(2), zones.get(1),users.get(1),"14445", states.get(1)));
		tickets.add(new Ticket(5,"ARS06", "Test ARS6", "Desc ARS6", applications.get(3), zones.get(3),users.get(1),"14445", states.get(1)));
		
		
		
		deleteAndInit(mongoService,DbUtils.index(User.class), users, x -> "user " + x.getLogin());
		
							
		deleteAndInit(mongoService, DbUtils.index(Ticket.class), tickets, t -> "ticket " + t.getReference());
		
		message.reply("OK");
	}
	
	

	private <T> void deleteAndInit(IMongoService mongoService,String index, List<T> liste, Function<T, String> message) {
		mongoService.delete(index, () -> {			
			for (T u : liste) {
				logger.debug("Before insert of " + message.apply(u));
				mongoService.insert(u, genericCallback(message.apply(u)));
			}
			
		});
	}
	
	
	
	
	private Handler<AsyncResult<String>> genericCallback(String message) {
		return x -> {
			if (x.succeeded()) {
				logger.debug("insertion SUCCEEDED for " +message );
			} else {
				
				logger.error("insertion FAILED for " +message  );
				logger.error(x.cause());
			}
		};
	}
	
	
}
