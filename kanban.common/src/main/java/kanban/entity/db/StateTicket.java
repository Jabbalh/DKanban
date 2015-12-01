package kanban.entity.db;

import java.util.UUID;

public class StateTicket {
	private String _id;
	private String code;
	private String libelle;	
	
	
	public StateTicket() {
		_id = UUID.randomUUID().toString();
	}
	
	
	
	public StateTicket(String code, String libelle) {
		this();
		this.code = code;
		this.libelle = libelle;
	}



	public String get_id() { return _id;}
	public void set_id(String _id) { this._id = _id;}



	public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}



	public String getLibelle() {
		return libelle;
	}



	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	
	
}
