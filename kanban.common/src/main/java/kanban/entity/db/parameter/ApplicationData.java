package kanban.entity.db.parameter;

import java.util.List;

public class ApplicationData {

	private static ApplicationParameter parameter = new ApplicationParameter();
	
	
	
	public static ApplicationParameter get() { return parameter; }
	public static void set(ApplicationParameter p) 
	{ 
		parameter.set_id(p.get_id());
		parameter.setApplications(p.getApplications());
		parameter.setStates(p.getStates());
		parameter.setInit(true);		
	}
	
	public static void setStateTickets(List<State> states) {
		get().setStates(states);
	}
}
