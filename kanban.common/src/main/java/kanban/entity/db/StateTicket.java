package kanban.entity.db;

import java.util.UUID;

public class StateTicket {
	private String _id;
	private String name;
	private String decription;	
	
	
	public StateTicket() {
		_id = UUID.randomUUID().toString();
	}
	
	public StateTicket(String name) {
		this();		
		this.name = name;
		//this._id = name;
		this.decription = "NA";	
	}		
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDecription() {
		return decription;
	}
	public void setDecription(String decription) {
		this.decription = decription;
	}
	
	
	public String get_id() { return _id;}
	public void set_id(String _id) { this._id = _id;}
	
	
}
