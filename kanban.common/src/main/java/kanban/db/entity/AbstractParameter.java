package kanban.db.entity;

import java.util.UUID;

public abstract class AbstractParameter {

	private String kanbanParameter;
	private String code;
	private String libelle;
	private String description;
	private String _id = UUID.randomUUID().toString();
	
	
	public AbstractParameter() {}
	
	public AbstractParameter(String kanbanParameter, String code, String libelle) {
		super();
		this.kanbanParameter = kanbanParameter;
		this.code = code;
		this.libelle = libelle;		
	}

	public String getKanbanParameter() {
		return kanbanParameter;
	}

	public void setKanbanParameter(String kanbanParameter) {
		this.kanbanParameter = kanbanParameter;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void clone(AbstractParameter clone){
		this.setCode(clone.getCode());
		this.setLibelle(clone.getLibelle());
		this.set_id(clone.get_id());
		this.setKanbanParameter(clone.getKanbanParameter());
	}
}
