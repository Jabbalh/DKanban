package kanban.service.utils;

import java.util.stream.Stream;

import kanban.entity.db.parameter.ApplicationData;
import kanban.entity.db.parameter.ZoneApp;

public class UiUtils {
	
	
	

	public static String otherZoneColumnId(String login,String state) {
		return String.format("%s$%s", login,state);
	}
	
	public static Integer getColumnWidth(String key) {
		return ApplicationData.get().getZoneByKey().get(key).getWidth();					
	}
	
	public static Integer getOrder(String key) {
		return ApplicationData.get().getZoneByKey().get(key).getOrder();
	}
	
	public static Stream<String> headers(){
		
		Stream<String> result = ApplicationData.get().getZones().stream().sorted((x,y) -> x.getOrder().compareTo(y.getOrder())).map(x -> x.getZoneTicket().getCodeZone());		
		return result;
	}
	
	public static Stream<ZoneApp> headersWithWidth(){
		
		Stream<ZoneApp> result = ApplicationData.get().getZones().stream().sorted((x,y) -> x.getOrder().compareTo(y.getOrder()));		
		return result;
	}
	
	public static ZoneApp getZoneApp(String key) {
		return ApplicationData.get().getZoneByKey().get(key);
	}
	
	
	
}
