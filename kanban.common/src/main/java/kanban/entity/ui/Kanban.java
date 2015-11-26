package kanban.entity.ui;

import java.util.List;

public class Kanban<T extends Card> {
	private List<SimpleColumn> headers;
	 
	private List<Zone<T>> zones;

	public Kanban(List<SimpleColumn> headers, List<Zone<T>> zones) {
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

	public List<Zone<T>> getZones() {
		return zones;
	}

	public void setZones(List<Zone<T>> zones) {
		this.zones = zones;
	}
	
	
	
}
