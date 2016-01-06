package kanban.db.entity;

import java.util.Date;
import java.util.function.Consumer;

/**
 * Historique d'un ticket
 *
 */
public class TicketHistory {

	private String _id;
	private String summary;
	private String description;
	private Date dateCreation;
	
	public TicketHistory() {}
	
	public TicketHistory(Consumer<TicketHistory> cstr){
		cstr.accept(this);
	}
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getDateCreation() {
		return dateCreation;
	}
	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}	
	
}
