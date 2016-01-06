package kanban.db.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class Ticket {

	private String _id;
	private String reference;
	private ParamTuple owner = new ParamTuple();
	private ParamTuple application = new ParamTuple();
	private ParamTuple zone = new ParamTuple();
	private Date dateCreation;
	private String summary;
	private String description;
	private String caisse;
	private ParamColorTuple statut = new ParamColorTuple();
	private ParamColorTuple priority = new ParamColorTuple();
	private List<TicketHistory> histories = new ArrayList<>(); 
	private Boolean archive = false;	
	
	
	public Ticket() {
		
	}
	
	public Ticket(User user) {
		owner.setCode(user.getLogin());
		owner.setLibelle(user.getFirstName()+  " " + user.getLastName());
		
	}
	
	public Ticket(Consumer<Ticket> constructor){
		constructor.accept(this);
	}
	
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;		
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public ParamTuple getOwner() {
		return owner;
	}
	public void setOwner(ParamTuple owner) {
		this.owner = owner;
	}
	public ParamTuple getApplication() {
		return application;
	}
	public void setApplication(ParamTuple application) {
		this.application = application;
	}
	public ParamTuple getZone() {
		return zone;
	}
	public void setZone(ParamTuple zone) {
		this.zone = zone;
	}
	public Date getDateCreation() {
		return dateCreation;
	}
	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
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
	public String getCaisse() {
		return caisse;
	}
	public void setCaisse(String caisse) {
		this.caisse = caisse;
	}
	public ParamColorTuple getStatut() {
		return statut;
	}
	public void setStatut(ParamColorTuple statut) {
		this.statut = statut;
	}

	public List<TicketHistory> getHistories() {
		return histories;
	}

	public void setHistories(List<TicketHistory> histories) {
		this.histories = histories;
	}

	public Boolean getArchive() {
		return archive;
	}

	public void setArchive(Boolean archive) {
		this.archive = archive;
	}

	

	

	public void addHistory(TicketHistory ticketHistory) {
		this.histories.add(ticketHistory);
		
	}

	public ParamColorTuple getPriority() {
		return priority;
	}

	public void setPriority(ParamColorTuple priority) {
		this.priority = priority;
	}
	
	
	
	
}
