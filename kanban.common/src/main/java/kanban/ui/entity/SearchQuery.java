package kanban.ui.entity;

import kanban.db.entity.ParamTuple;

public class SearchQuery {

	private String reference = "";
	private ParamTuple owner = null;
	private String description = "";
	private ParamTuple application = null;
	private Boolean archive = false;


	public Boolean getArchive() {return archive; }
	public void setArchive(Boolean archive) { this.archive = archive; 	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public ParamTuple getOwner() {
		return owner;
	}
	public void setOwner(ParamTuple owner) {
		this.owner = owner;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ParamTuple getApplication() {
		return application;
	}
	public void setApplication(ParamTuple application) {
		this.application = application;
	}
	
	
	
}
