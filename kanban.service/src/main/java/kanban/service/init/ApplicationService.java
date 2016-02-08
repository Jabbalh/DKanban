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
		Async.When(()-> mongoService.findAll(ApplicationParameter.class))	.doThat(application -> ApplicationData.get().setApplications(application));
		Async.When(()-> mongoService.findAll(StatutParameter.class))		.doThat(status 		-> ApplicationData.get().setStatut(status));
		Async.When(()-> mongoService.findAll(ZoneParameter.class))			.doThat(zones 		-> ApplicationData.get().setZones(zones));
		Async.When(()-> mongoService.findAll(PriorityParameter.class))		.doThat(priority 	-> ApplicationData.get().setPriority(priority));
		Async.When(()-> mongoService.findAll(VersionParameter.class))		.doThat(version 	-> ApplicationData.get().setVersions(version));

	}
	
	/**
	 * Initialisation des paramètres (mode dev ou first)
	 */
	private void initParameter(Message<Object> message){	
		logger.debug("INIT_FIRST_APP -> initParameter");
		List<ApplicationParameter> applications = new ArrayList<>();
		applications.add(new ApplicationParameter("DEI PART", "DEI PART"));
		applications.add(new ApplicationParameter("DEI PRO", "DEI PRO"));
		applications.add(new ApplicationParameter("DEI PP", "DEI PP"));
		applications.add(new ApplicationParameter("DEA", "DEA"));
		applications.add(new ApplicationParameter("NOSA WEB", "NOSA WEB"));
		applications.add(new ApplicationParameter("SVI", "SVI"));
		applications.add(new ApplicationParameter("CFN", "CFN"));
		applications.add(new ApplicationParameter("EVI", "EVI"));
		
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
		statesTicket.add(new StatutParameter(s -> {s.setCode("IN_PROGRESS"); 		s.setLibelle("En cours"); 			s.setColor("#008000"); }));
		statesTicket.add(new StatutParameter(s -> {s.setCode("AFFECTE"); 			s.setLibelle("Affecte"); 			s.setColor("#2F4F4F"); }));
		statesTicket.add(new StatutParameter(s -> {s.setCode("STAND_BY"); 			s.setLibelle("En attente"); 		s.setColor("#FF8C00"); }));
		statesTicket.add(new StatutParameter(s -> {s.setCode("FINISH"); 			s.setLibelle("Resolu"); 			s.setColor("#A9A9A9"); }));
		statesTicket.add(new StatutParameter(s -> {s.setCode("STAND_BY_CAISSE"); 	s.setLibelle("En attente caisse"); 	s.setColor("#FFA07A"); }));
		ApplicationData.get().setStatut(statesTicket);


		List<PriorityParameter> priorities = new ArrayList<>();
		priorities.add(new PriorityParameter(p -> { p.setCode("PO"); p.setLibelle("Critique"); 		p.setColor("#FF0000"); }));
		priorities.add(new PriorityParameter(p -> { p.setCode("P1"); p.setLibelle("Importante"); 	p.setColor("#FFA500"); }));
		priorities.add(new PriorityParameter(p -> { p.setCode("P2"); p.setLibelle("Moyenne"); 		p.setColor("#F4A460"); }));
		priorities.add(new PriorityParameter(p -> { p.setCode("P3"); p.setLibelle("Faible"); 		p.setColor("#F5DEB3"); }));
		ApplicationData.get().setPriority(priorities);
		
		if (!ApplicationData.isInit){
			mongoService.delete(DbUtils.index(KanbanParameter.class), () -> {
				deleteAndInit(mongoService, ApplicationParameter.class, applications, 	a -> "ApplicationParameter "+ a.getCode());
				deleteAndInit(mongoService, StatutParameter.class, 		statesTicket, 	a -> "StatutParameter " 	+ a.getCode());
				deleteAndInit(mongoService, ZoneParameter.class, 		zoneApps, 		a -> "ZoneParameter " 		+ a.getCode());
				deleteAndInit(mongoService, PriorityParameter.class, 	priorities, 	a -> "PriorityParameter " 	+ a.getCode());
			});
		}

		mongoService.reinitCounters();
		
		
		message.reply("OK");
	}
	
	
	/***
	 * Initialisation des données (mode Dev)
	 */
	private void initData(Message<Object> message) {
		logger.debug("INIT_DATA_APP -> initData");

		List<User> users = new LinkedList<>();
		users.add(new User("NIHU", cryptoService.genHash256("NIHU"), "Nicolas", "HUET"));
		users.add(new User("GUTA", cryptoService.genHash256("GUTA"), "Guillaume", "TASSET"));
		users.add(new User("ISHE", cryptoService.genHash256("ISHE"), "Isabelle", "HEMONIC"));
		users.add(new User("CAPI", cryptoService.genHash256("CAPI"), "Catherine", "PICARDA"));
		users.add(new User("QUGU", cryptoService.genHash256("QUGU"), "Quentin", "GUILLEE"));
		

		
		deleteAndInit(mongoService,User.class, users, x -> "user " + x.getLogin());
		
		
		mongoService.delete(DbUtils.index(Ticket.class), () -> mongoService.createIndex(indexCreated -> {
            if (indexCreated) {

				System.out.println("Init index done...");
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
