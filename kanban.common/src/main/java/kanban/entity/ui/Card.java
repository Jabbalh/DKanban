package kanban.entity.ui;

public class Card {
	private String ref;
	private String id;

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
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	
}
