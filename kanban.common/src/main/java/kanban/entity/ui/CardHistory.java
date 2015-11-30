package kanban.entity.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import kanban.entity.db.TicketHistory;

public class CardHistory {
	private String summary;
	private String description;
	private String date;
	
	
	
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
	
	
	
	public static CardHistory fromTicketHistory(TicketHistory ticketHistory){
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		CardHistory result = new CardHistory();
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


}
