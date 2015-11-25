package kanban.entity.db;

import java.util.UUID;

public class ZoneTicket {
	private String codeZone;	
	private String _id;
	
	public ZoneTicket() 
	{ 
		
	}
	
	public ZoneTicket(String codeZone) {
		super();
		this.codeZone = codeZone;		
		_id = UUID.randomUUID().toString();
	}
	public String getCodeZone() {
		return codeZone;
	}
	public void setCodeZone(String codeZone) {
		this.codeZone = codeZone;
	}
	

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}
	
	
}
