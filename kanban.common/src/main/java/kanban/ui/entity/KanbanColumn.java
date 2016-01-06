package kanban.ui.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import kanban.db.entity.Ticket;
import kanban.db.entity.ZoneParameter;

public class KanbanColumn {

	private String id;
	private ZoneParameter zone;
	private List<Ticket> tickets = new ArrayList<>();
	
	public KanbanColumn() {}
	
	public KanbanColumn(Consumer<KanbanColumn> cstr) 
	{
		cstr.accept(this);
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ZoneParameter getZone() {
		return zone;
	}
	public void setZone(ZoneParameter zone) {
		this.zone = zone;
	}
	public List<Ticket> getTickets() {
		return tickets;
	}
	public void setTickets(List<Ticket> tickets) {
		this.tickets = tickets;
	}
}
