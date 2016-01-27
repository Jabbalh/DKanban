package kanban.db.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Contient les paramètres de l'application
 *
 */
public class KanbanParameter {

	public static String EXPOSED_ID = "APPLI_PARAMTER";
	private String _id = EXPOSED_ID;
	private String title;
	private List<ZoneParameter> zones = new ArrayList<>();
	private List<ApplicationParameter> applications = new ArrayList<>();
	private List<StatutParameter> statut = new ArrayList<>();
	private List<PriorityParameter> priority = new ArrayList<>();
	private List<VersionParameter> versions = new ArrayList<>();
	public KanbanParameter() {}
	
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<ZoneParameter> getZones() {
		return zones;
	}
	public void setZones(List<ZoneParameter> zones) {
		this.zones = zones;
	}
	public List<ApplicationParameter> getApplications() {
		return applications;
	}
	public void setApplications(List<ApplicationParameter> applications) {
		this.applications = applications;
	}
	public List<StatutParameter> getStatut() {
		return statut;
	}
	public void setStatut(List<StatutParameter> statut) {
		this.statut = statut;
	}


	public List<PriorityParameter> getPriority() {
		return priority;
	}


	public void setPriority(List<PriorityParameter> priority) {
		this.priority = priority;
	}


	public List<VersionParameter> getVersions() {
		return versions;
	}

	public void setVersions(List<VersionParameter> versions) {
		this.versions = versions;
	}
}
