package kanban.entity.ui;

import java.util.List;
import java.util.function.Consumer;

import kanban.entity.db.Ticket;

public class CardTicket extends Card {
	
	private String appli;
	private String summary;
	private String description;
	private String caisse;
	private String state;
	private String owner;
	private List<CardHistory> history;
	
	
	public CardTicket() {
		
	}
	
	public CardTicket(String owner){
		super();
		this.owner = owner;
	}
	
	public CardTicket(String id, String ref, String appli, String summary, String description, String caisse,
			String state, String owner) {
		super(id, ref);
		this.appli = appli;
		this.summary = summary;
		this.description = description;
		this.caisse = caisse;
		this.state = state;
		this.owner = owner;
	}
	
	public CardTicket(Consumer<CardTicket> f) {
		f.accept(this);
	}
	
	

	
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getAppli() {
		return appli;
	}

	public void setAppli(String appli) {
		this.appli = appli;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCaisse() {
		return this.caisse;
	}

	public void setCaisse(String caisse) {
		this.caisse = caisse;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public List<CardHistory> getHistory() {
		return history;
	}

	public void setHistory(List<CardHistory> history) {
		this.history = history;
	}
	
	public static CardTicket fromTicket(Ticket ticket) {
		CardTicket result = new CardTicket();
		
		result.setId(ticket.get_id());
		result.setAppli(ticket.getApplication().getName());
		result.setCaisse(ticket.getCaisse());
		result.setDescription(ticket.getDescription());
		result.setHistory(CardHistory.fromTicketHistory(ticket.getTicketHistory()));
		result.setOwner(ticket.getOwner().getLogin());
		result.setRef(ticket.getReference());
		result.setState(ticket.getStateTicket().getName());
		result.setSummary(ticket.getSummary());
		
		return result;
	}

	
}
