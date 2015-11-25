package kanban.entity.db;

import java.util.Date;

public class TicketHistory {
	private String summary;
	private String description;
	private Date date;
	
	private String _id;
	
	
	public TicketHistory() {
		super();
		this._id = String.valueOf(new Date().getTime());
	}
	public TicketHistory(String summary, Date date) {
		this();
		this.summary = summary;
		this.date = date;
	}
	public TicketHistory(String summary, String description, Date date) {
		this();
		this.summary = summary;
		this.description = description;
		this.date = date;
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
}
