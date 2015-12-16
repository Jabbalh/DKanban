package kanban.entity.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import kanban.entity.db.TicketHistory;
import kanban.utils.tools.Tools;

public class CardHistory {
	private String summary;
	private String description;
	private String date;
	private String id;
	
	
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public static CardHistory fromTicketHistory(TicketHistory ticketHistory){
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		CardHistory result = new CardHistory();
		result.id = ticketHistory.get_id();
		result.summary = ticketHistory.getSummary();
		result.description = ticketHistory.getDescription();
		result.date = sdf.format(ticketHistory.getDate());
		return result;
		
	}
	
	public static List<CardHistory> fromTicketHistory(List<TicketHistory> ticketHistory){	
		return (ticketHistory != null) 
					? ticketHistory.stream().map(x -> fromTicketHistory(x)).collect(Collectors.toList())
					: new ArrayList<>();
		}

	public TicketHistory toTicketHistory(){
		TicketHistory result = new TicketHistory();
		result.set_id(this.getId());
		result.setSummary(this.getSummary());
		result.setDescription(this.getDescription());
		System.out.println("toTicketHistory -> " + this.getDate());
		result.setDate(Tools.parseDate(this.getDate()));
		return result;
	}
	
	


}
