package fr.kanban.front;

import io.vertx.core.Vertx;

public class AbstractHandler {
	protected Vertx vertx;
	
	
	public AbstractHandler(){
		this.vertx = Vertx.currentContext().owner();
	}
}
