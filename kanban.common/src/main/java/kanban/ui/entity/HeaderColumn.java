package kanban.ui.entity;

public class HeaderColumn {

	private Integer width;
	private String libelle;
	private Integer order;
	private String code;
	
	
	public HeaderColumn(Integer width, String libelle, Integer order) {
		super();
		this.width = width;
		this.libelle = libelle;
		this.order = order;
	}

	public HeaderColumn(Integer width, String libelle, Integer order, String code) {
		super();
		this.width = width;
		this.libelle = libelle;
		this.order = order;
		this.code = code;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
