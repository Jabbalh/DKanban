package kanban.entity.ui;

import java.util.Date;

public class Card {
	private String ref;
	private String id;

	public Card(){
		
	}
	
	public Card(String id,String ref) {
		super();
		this.ref = ref;
		this.id = id;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getId() {
		if (id == null){
			id = String.valueOf(new Date().getTime());
		}
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	
	
}
