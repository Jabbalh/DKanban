package kanban.entity.db.parameter;

import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kanban.entity.db.Application;
import kanban.entity.db.StateTicket;

public class ApplicationParameter {
	
	private String _id;
	
	private List<ZoneApp> zones;
	private List<Application> applications;
	private List<StateTicket> statesTicket;
	private boolean isInit = false;
	
	@JsonIgnore	
	private LinkedHashMap<String, ZoneApp> zoneByKey;
	
	
	public ApplicationParameter() {
		_id = "APPLI_PARAMTER";
	}
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	
	public boolean isInit() {
		return isInit;
	}
	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}
	public List<ZoneApp> getZones() {
		return zones;
	}
	public void setZones(List<ZoneApp> zones) {
		this.zones = zones;
		this.initStateByKey();
	}
	public List<Application> getApplications() {
		return applications;
	}
	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}
	
	
	private void initStateByKey(){
		zoneByKey = new LinkedHashMap<>();
		zones.forEach(x -> zoneByKey.put(x.getZoneTicket().getCodeZone(), x));		
	}
	
	@JsonIgnore
	public LinkedHashMap<String, ZoneApp> getZoneByKey() { return zoneByKey; }

	public List<StateTicket> getStatesTicket() {
		return statesTicket;
	}

	public void setStatesTicket(List<StateTicket> statesTicket) {
		this.statesTicket = statesTicket;
	}
	
}
