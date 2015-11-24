package kanban.entity.db;

public class Ticket {
	
	private String reference;
	private String summary;
	private String description;
	private Application application;
	private StateTicket stateTicket;
	private User owner;
	
	
	
	public Ticket() {
	
	}
	
	public Ticket(String reference, String summary, String description, Application application, StateTicket state,
			User owner) {
		this();
		this.reference = reference;
		this.summary = summary;
		this.description = description;
		this.application = application;
		this.stateTicket = state;
		this.owner = owner;
		this._id = reference;
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
	
}
