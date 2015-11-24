package kanban.entity.ui;

import java.util.List;

public class Kanban {
	private List<SimpleColumn> headers;
	 
	private List<Zone> zones;

	public Kanban(List<SimpleColumn> headers, List<Zone> zones) {
		super();
		this.headers = headers;
		this.zones = zones;
	}

	public List<SimpleColumn> getHeaders() {
		return headers;
	}

	public void setHeaders(List<SimpleColumn> headers) {
		this.headers = headers;
	}

	public List<Zone> getZones() {
		return zones;
	}

	public void setZones(List<Zone> zones) {
		this.zones = zones;
	}
	
	
	
}
