package kanban.entity.db.parameter;

import kanban.entity.db.ZoneTicket;

public class ZoneApp {
	private ZoneTicket zoneTicket;
	private Integer width;
	private Integer order;
	
	public ZoneApp(){}
	
	public ZoneApp(ZoneTicket zoneTicket, Integer order, Integer width) {
		super();
		this.zoneTicket = zoneTicket;
		this.width = width;
		this.order = order;
	}

	public ZoneTicket getZoneTicket() {
		return zoneTicket;
	}

	public void setZoneTicket(ZoneTicket zoneTicket) {
		this.zoneTicket = zoneTicket;
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
