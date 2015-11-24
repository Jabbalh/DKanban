package kanban.entity.db.parameter;

import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kanban.entity.db.Application;

public class ApplicationParameter {
	
	private String _id;
	
	private List<State> states;
	private List<Application> applications;
	private boolean isInit = false;
	
	@JsonIgnore	
	private LinkedHashMap<String, State> stateByKey;
	
	
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
	public List<State> getStates() {
		return states;
	}
	public void setStates(List<State> states) {
		this.states = states;
		this.initStateByKey();
	}
	public List<Application> getApplications() {
		return applications;
	}
	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}
	
	
	private void initStateByKey(){
		stateByKey = new LinkedHashMap<>();
		states.forEach(x -> stateByKey.put(x.getStateTicket().getName(), x));		
	}
	
	@JsonIgnore
	public LinkedHashMap<String, State> getStateByKey() { return stateByKey; }
	
}
