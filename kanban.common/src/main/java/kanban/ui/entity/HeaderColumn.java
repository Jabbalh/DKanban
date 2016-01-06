package kanban.ui.entity;

public class HeaderColumn {

	private Integer width;
	private String libelle;
	private Integer order;
	
	
	
	public HeaderColumn(Integer width, String libelle, Integer order) {
		super();
		this.width = width;
		this.libelle = libelle;
		this.order = order;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
}
