package kanban.entity.db;

import java.util.ArrayList;
import java.util.List;

public class Ticket {
	
	private String reference;
	private String summary;
	private String description;
	private Application application;
	private StateTicket stateTicket = new StateTicket();
	private String caisse;
	private ZoneTicket zoneTicket;
	private User owner;
	private List<TicketHistory> ticketHistory;
	private Boolean archive;
	
	
	
	public Ticket() {
		archive = false;
	}
	
	public Ticket(String reference, String summary, String description, Application application, ZoneTicket zone,
			User owner, String caisse) {
		this();
		this.reference = reference;
		this.summary = summary;
		this.description = description;
		this.application = application;
		this.zoneTicket = zone;
		this.owner = owner;
		this.caisse = caisse;
		//this._id = reference;
	}
	
	public Ticket(Integer id,String reference, String summary, String description, Application application, ZoneTicket zone,
			User owner, String caisse) {
		this();
		this.reference = reference;
		this.summary = summary;
		this.description = description;
		this.application = application;
		this.zoneTicket = zone;
		this.owner = owner;
		this.caisse = caisse;
		this._id = id.toString();
	}
	
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
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
	public Application getApplication() {
		return application;
	}
	public void setApplication(Application application) {
		this.application = application;
	}
	public StateTicket getStateTicket() {
		return stateTicket;
	}
	public void setStateTicket(StateTicket state) {
		this.stateTicket = state;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	private String _id;
	public String get_id() { return _id;}
	public void set_id(String _id) { this._id = _id;}

	public ZoneTicket getZoneTicket() {
		return zoneTicket;
	}

	public void setZoneTicket(ZoneTicket zoneTicket) {
		this.zoneTicket = zoneTicket;
	}

	public String getCaisse() {
		return caisse;
	}

	public void setCaisse(String caisse) {
		this.caisse = caisse;
	}

	public List<TicketHistory> getTicketHistory() {
		return ticketHistory;
	}

	public void setTicketHistory(List<TicketHistory> ticketHistory) {
		this.ticketHistory = ticketHistory;
	}
	
	public void addHistory(TicketHistory history){
		if (ticketHistory == null){
			this.ticketHistory = new ArrayList<TicketHistory>();
		}
		ticketHistory.add(history);
	}

	public Boolean getArchive() {
		return archive;
	}

	public void setArchive(Boolean archive) {
		this.archive = archive;
	}
	
}
