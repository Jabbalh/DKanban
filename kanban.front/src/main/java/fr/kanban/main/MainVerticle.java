package fr.kanban.main;

import fr.kanban.front.FrontVerticle;
import io.vertx.core.AbstractVerticle;
import kanban.service.VerticleKanbanService;
import kanban.service.VerticleTicketService;
import kanban.service.VerticleUserService;
import kanban.service.init.ApplicationService;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start() {

		
		VerticleUtils.DeployeVertical(vertx,ApplicationService.class, x -> {
			if (x.succeeded()){
				vertx.eventBus().send(ApplicationService.INIT_FIRST_APP, "do", r -> {
					vertx.eventBus().send(ApplicationService.INIT_DATA_APP, "go");
				});
				deploy();
			}
		});	
		
	/*
		VerticleUtils.DeployeVertical(vertx,ApplicationService.class, x -> {
			if (x.succeeded()){
				vertx.eventBus().send(ApplicationService.INIT_APPLICATION, "do", r -> {
					//vertx.eventBus().send(ApplicationService.INIT_DATA_APP, "go");
				});
				deploy();
			}
		});	
	*/
		
	}
	
	private void deploy() {
		VerticleUtils.DeployeVertical(vertx, FrontVerticle.class);
		VerticleUtils.DeployeVertical(vertx, VerticleKanbanService.class);		
		VerticleUtils.DeployeVertical(vertx, VerticleTicketService.class);
		VerticleUtils.DeployeVertical(vertx, VerticleUserService.class);
	}
	
}
