package kanban.entity.ui;

public class CardAppli extends Card {

	private Integer ticketCount;
	
	public CardAppli(String id,String ref, Integer ticketCount) {
		super(id,ref);
		this.ticketCount = ticketCount;
	}

	public Integer getTicketCount() {
		return ticketCount;
	}

	public void setTicketCount(Integer ticketCount) {
		this.ticketCount = ticketCount;
	}

	
	
}
