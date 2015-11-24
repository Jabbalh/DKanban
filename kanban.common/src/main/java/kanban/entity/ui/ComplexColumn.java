package kanban.entity.ui;


import java.util.ArrayList;
import java.util.List;

public class ComplexColumn<T extends Card> {
	
	private String id;
	private Integer width;
	private List<T> cards;
	
	
	public ComplexColumn(String id, Integer width) {
		
		this.id = id;
		this.width = width;		
		cards = new ArrayList<>();
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public List<T> getCards() {
		return cards;
	}
	public void setCards(List<T> cards) {
		this.cards = cards;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public void addCard(T card){
		this.cards.add(card);
	}
	
	
}
