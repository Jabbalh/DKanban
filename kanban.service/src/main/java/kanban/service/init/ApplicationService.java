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
import kanban.db.entity.*;
import kanban.entity.session.ApplicationData;
import kanban.service.contract.ICryptoService;
import kanban.service.contract.IMongoService;
import kanban.service.utils.DbUtils;
import kanban.utils.callback.Async;

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
		
		vertx.eventBus().consumer(INIT_FIRST_APP, 	this::initParameter );
		vertx.eventBus().consumer(INIT_DATA_APP, 	this::initData);
		vertx.eventBus().consumer(INIT_APPLICATION,	this::initApplication);
		
		logger.debug("ApplicationService .. run ...");
		
	}
	
	/**
	 * Initialisation de l'application
	 */
	private void initApplication(Message<Object> message) {
		logger.debug("INIT_APPLICATION -> initApplication");
		 ApplicationData.set(new KanbanParameter());
		Async.When(()-> mongoService.findAll(ApplicationParameter.class)).doThat(application -> ApplicationData.get().setApplications(application));
		Async.When(()-> mongoService.findAll(StatutParameter.class)).doThat(status -> ApplicationData.get().setStatut(status));
		Async.When(()-> mongoService.findAll(ZoneParameter.class)).doThat(zones -> ApplicationData.get().setZones(zones));
		Async.When(()-> mongoService.findAll(PriorityParameter.class)).doThat(priority -> ApplicationData.get().setPriority(priority));
		Async.When(()-> mongoService.findAll(VersionParameter.class)).doThat(version -> ApplicationData.get().setVersions(version));
			/*
		Async.When(() -> mongoService.findAll(User.class))
		.doThat(users -> {
			for (User u : users) {
				u.setPassword(cryptoService.genHash256(u.getLogin()));
				Async.When(() -> mongoService.update(u)).doThat(b -> logger.info("User " + u.getLogin() + " update " + b));					
			}
		});		
		*/
		/*
		Async.When(() -> mongoService.findAll(KanbanParameter.class))
		.doThat( x -> {
			
			ApplicationData.set(x.get(0));	
			message.reply("OK");
			
			Async.When(() -> mongoService.findAll(User.class))
			.doThat(users -> {
				for (User u : users) {
					u.setPassword(cryptoService.genHash256(u.getLogin()));
					Async.When(() -> mongoService.update(u)).doThat(b -> System.out.println("User " + u.getLogin() + " update " + b));					
				}
			});
			
		});
		*/
	}
	
	/**
	 * Initialisation des paramètres (mode dev ou first)
	 */
	private void initParameter(Message<Object> message){	
		logger.debug("INIT_FIRST_APP -> initParameter");
		List<ApplicationParameter> applications = new ArrayList<>();
		applications.add(new ApplicationParameter("DEI PART", "DEI PART"));
		applications.add(new ApplicationParameter("DEI PRO", "DEI PRO"));
		applications.add(new ApplicationParameter("DEA", "DEA"));
		applications.add(new ApplicationParameter("NOSA WEB", "NOSA WEB"));
		applications.add(new ApplicationParameter("SVI", "SVI"));
		
		ApplicationData.set(new KanbanParameter());
		ApplicationData.get().setApplications(applications);
				
		
		List<ZoneParameter> zoneApps = new ArrayList<>();
		zoneApps.add(new ZoneParameter(x -> {
			x.setCode("U");x.setOrder(0);x.setWidth(1);x.setLibelle("U");x.setDroppableArchive(true);x.setDroppableTicket(false);
		}));
		
		zoneApps.add(new ZoneParameter(x -> {
			x.setCode("BackLog");x.setOrder(1);x.setWidth(2);x.setLibelle("BackLog");x.setDroppableArchive(false);x.setDroppableTicket(true);
		}));
		
		zoneApps.add(new ZoneParameter(x -> {
			x.setCode("Analyse");x.setOrder(2);x.setWidth(2);x.setLibelle("Analyse");x.setDroppableArchive(false);x.setDroppableTicket(true);
		}));
		
		zoneApps.add(new ZoneParameter(x -> {
			x.setCode("Dev");x.setOrder(3);x.setWidth(2);x.setLibelle("Dev");x.setDroppableArchive(false);x.setDroppableTicket(true);
		}));
				
		zoneApps.add(new ZoneParameter(x -> {
			x.setCode("VFO");x.setOrder(4);x.setWidth(2);x.setLibelle("VFO");x.setDroppableArchive(false);x.setDroppableTicket(true);
		}));
		
		zoneApps.add(new ZoneParameter(x -> {
			x.setCode("UTI");x.setOrder(5);x.setWidth(2);x.setLibelle("UTI");x.setDroppableArchive(false);x.setDroppableTicket(true);
		}));
		
		zoneApps.add(new ZoneParameter(x -> {
			x.setCode("QPA");x.setOrder(6);x.setWidth(2);x.setLibelle("QPA");x.setDroppableArchive(false);x.setDroppableTicket(true);
		}));
		
		zoneApps.add(new ZoneParameter(x -> {
			x.setCode("PROD");x.setOrder(7);x.setWidth(2);x.setLibelle("PROD");x.setDroppableArchive(false);x.setDroppableTicket(true);
		}));
				
		ApplicationData.get().setZones(zoneApps);
		
		
		List<StatutParameter> statesTicket = new ArrayList<>();
		statesTicket.add(new StatutParameter("IN_PROGRESS", "En cours"));
		statesTicket.add(new StatutParameter("STAND_BY", "En attente"));
		ApplicationData.get().setStatut(statesTicket);
		
		
		if (!ApplicationData.isInit){
			mongoService.delete(DbUtils.index(KanbanParameter.class), () -> {
				
				/*Async.When(() -> mongoService.insert(ApplicationData.get()))
						.Rule(rule -> rule)
						.Otherwise(other -> { logger.error("Application NOT initialized -> " + other); message.reply("NOK"); })
						.doThat(x -> {									
							logger.debug("Application initialized");
							ApplicationData.isInit = true;
							message.reply("OK");					
				});*/
				
				deleteAndInit(mongoService, ApplicationParameter.class, applications, a -> "ApplicationParameter " + a.getCode()); 
				deleteAndInit(mongoService, StatutParameter.class, statesTicket, a -> "StatutParameter " + a.getCode());
				deleteAndInit(mongoService, ZoneParameter.class, zoneApps, a -> "ZoneParameter " + a.getCode());
			});
		}
		
		/*
		List<ZoneTicket> zoneTicket = new ArrayList<>();
		
		ApplicationData.get().getZones().forEach(x -> zoneTicket.add(x.getZoneTicket()));
		
		deleteAndInit(mongoService,DbUtils.index(Application.class), applications, a -> "application " + a.getName());
		deleteAndInit(mongoService,DbUtils.index(ZoneTicket.class), zoneTicket, s -> "zone "+s.getCodeZone());
		deleteAndInit(mongoService,DbUtils.index(StateTicket.class), statesTicket, s -> "state "+s.getCode());
		*/
		
		mongoService.reinitCounters();
		
		
		message.reply("OK");
	}
	
	
	/***
	 * Initialisation des données (mode Dev)
	 */
	private void initData(Message<Object> message) {
		logger.debug("INIT_DATA_APP -> initData");
		//List<Application> applications = ApplicationData.get().getApplications();
		//List<ZoneTicket> zones = new ArrayList<>();
		
		//ApplicationData.get().getZones().forEach(x -> zones.add(x.getZoneTicket()));
				
		List<User> users = new LinkedList<>();
		users.add(new User("user1", cryptoService.genHash256("user1"), "User 1", "User 1"));
		users.add(new User("user2", cryptoService.genHash256("user2"), "User 2", "User 2"));
		
		//List<StateTicket> states = ApplicationData.get().getStatesTicket();
				
		List<Ticket> tickets = new LinkedList<>();				
		tickets.add(
				new Ticket(
						x -> {
							x.set_id("0");
							x.setReference("ARS01");
							x.setSummary("Test ARS1");
							x.setDescription("Desc ARS1");
							x.setApplication(new ParamTuple("DEI PART", "DEI PART"));
							x.setZone(new ParamTuple("BackLog", "BackLog"));
							x.setOwner(new ParamTuple("user1", "user1 user1"));
							x.setCaisse("14445");
							x.setStatut(new ParamColorTuple("IN_PROGRESS","En cours"));
						}						
				));
		tickets.add(
				new Ticket(
						x -> {
							x.set_id("1");
							x.setReference("ARS02");
							x.setSummary("Test ARS2");
							x.setDescription("Desc ARS2");
							x.setApplication(new ParamTuple("DEI PART", "DEI PART"));
							x.setZone(new ParamTuple("BackLog", "BackLog"));
							x.setOwner(new ParamTuple("user1", "user1 user1"));
							x.setCaisse("14445");
							x.setStatut(new ParamColorTuple("IN_PROGRESS","En cours"));
						}						
				));
		tickets.add(
				new Ticket(
						x -> {
							x.set_id("2");
							x.setReference("ARS03");
							x.setSummary("Test ARS3");
							x.setDescription("Desc ARS3");
							x.setApplication(new ParamTuple("DEI PRO", "DEI PRO"));
							x.setZone(new ParamTuple("VFO", "VFO"));
							x.setOwner(new ParamTuple("user2", "user2 user2"));
							x.setCaisse("14445");
							x.setStatut(new ParamColorTuple("STAND_BY","En attente"));
						}						
				));
		
		
		
		
		deleteAndInit(mongoService,User.class, users, x -> "user " + x.getLogin());
		
		
		mongoService.delete(DbUtils.index(Ticket.class), () -> mongoService.createIndex(indexCreated -> {
            if (indexCreated) {
                for (Ticket u : tickets) {
                    Async.When(() -> mongoService.insert(u)).doThat(x ->  genericCallback("Ticket" + u.getReference()));
                }
            }
        }));
		
		
						
		
		
		message.reply("OK");
	}
	
	

	private <T> void deleteAndInit(IMongoService mongoService,Class<T> clazz, List<T> liste, Function<T, String> message) {
		mongoService.delete(DbUtils.index(clazz), () -> {			
			for (T u : liste) {
				logger.debug("Before insert of " + message.apply(u));
				Async.When(() -> mongoService.insert(u)).doThat(x ->  genericCallback(message.apply(u)));
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
