package kanban.entity.ui;

import java.util.ArrayList;
import java.util.List;


public class Zone<T extends Card> {
	private SimpleColumn first;
	private List<ComplexColumn<T>> other;

	public Zone() {
		other = new ArrayList<>();
	}

	public SimpleColumn getFirst() {
		return first;
	}

	public void setFirst(SimpleColumn first) {
		this.first = first;
	}

	public List<ComplexColumn<T>> getOther() {
		return other;
	}

	public void setOther(List<ComplexColumn<T>> other) {
		this.other = other;
	}
	
	public void addOther(ComplexColumn<T> column){
		this.other.add(column);
	}
	
	
	
}
