package kanban.entity.ui;

public class FullCard {
	private String zone;
	private CardTicket card;
	private String user;
	private Boolean insert;
	private String title;
	
	
	public FullCard() {}
	
	public FullCard(String zone, CardTicket card, String user) {
		super();
		this.zone = zone;
		this.card = card;
		this.user = user;
	}
	
	
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public CardTicket getCard() {
		return card;
	}
	public void setCard(CardTicket card) {
		this.card = card;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}

	public Boolean getInsert() {
		return insert;
	}

	public void setInsert(Boolean insert) {
		this.insert = insert;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
