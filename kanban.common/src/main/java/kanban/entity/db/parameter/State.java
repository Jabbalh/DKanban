package kanban.entity.db.parameter;

import kanban.entity.db.StateTicket;

public class State {
	private StateTicket stateTicket;
	private Integer width;
	private Integer order;
	
	public State(){}
	
	public State(StateTicket stateTicket, Integer order, Integer width) {
		super();
		this.stateTicket = stateTicket;
		this.width = width;
		this.order = order;
	}

	public StateTicket getStateTicket() {
		return stateTicket;
	}

	public void setStateTicket(StateTicket stateTicket) {
		this.stateTicket = stateTicket;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
	
	
}
