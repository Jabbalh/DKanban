package kanban.entity.db.parameter;

import java.util.List;

public class ApplicationData {

	private static ApplicationParameter parameter = new ApplicationParameter();
	
	
	
	public static ApplicationParameter get() { return parameter; }
	public static void set(ApplicationParameter p) 
	{ 
		parameter.set_id(p.get_id());
		parameter.setApplications(p.getApplications());
		parameter.setZones(p.getZones());
		parameter.setInit(true);		
	}
	
	public static void setStateTickets(List<ZoneApp> states) {
		get().setZones(states);
	}
}
