package kanban.db.entity;

import java.util.function.Consumer;

/**
 * Représente la zone dans laquelle se trouve un ticket
 *
 */
public class ZoneParameter extends AbstractParameter {
	
	private Integer order;
	private Integer width;
	private Boolean droppableTicket;
	private Boolean droppableArchive;
	
	public ZoneParameter() {
		this.setKanbanParameter(KanbanParameter.EXPOSED_ID);
	}
	
	public ZoneParameter(Consumer<ZoneParameter> cstr){
		this();
		cstr.accept(this);
	}

	

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Boolean getDroppableTicket() {
		return droppableTicket;
	}

	public void setDroppableTicket(Boolean droppableTicket) {
		this.droppableTicket = droppableTicket;
	}

	public Boolean getDroppableArchive() {
		return droppableArchive;
	}

	public void setDroppableArchive(Boolean droppableArchive) {
		this.droppableArchive = droppableArchive;
	}
	
}
