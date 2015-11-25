package kanban.entity.ui;

public class CardTicket extends Card {
	
	private String appli;
	private String summary;
	private String description;
	private String caisse;
	private String state;
	private String owner;
	
	
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

	
}
