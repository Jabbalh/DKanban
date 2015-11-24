package kanban.entity.ui;

public class SimpleColumn {
	private String libelle;
	private Integer width;
	private String id;
	
	
	public SimpleColumn(String id,String libelle, Integer width) {
		super();
		this.libelle = libelle;
		this.width = width;
		this.id = id;
	}
	
	public SimpleColumn(String libelle, Integer width) {
		super();
		this.libelle = libelle;
		this.width = width;
		this.id = "NA";
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	
}
