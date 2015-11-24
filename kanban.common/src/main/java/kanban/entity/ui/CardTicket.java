package kanban.entity.ui;

public class CardTicket extends Card {
	
	private String appli;
	private String summary;
	
	public CardTicket(String id,String ref, String appli, String summary) {
		super(id,ref);
		this.appli = appli;
		this.summary = summary;
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

	
}
