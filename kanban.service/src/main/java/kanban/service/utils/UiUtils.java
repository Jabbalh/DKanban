package kanban.service.utils;

import java.util.stream.Stream;

import kanban.entity.db.parameter.ApplicationData;
import kanban.entity.db.parameter.State;

public class UiUtils {

	public static String otherZoneColumnId(String login,String state) {
		return String.format("%s$%s", login,state);
	}
	
	public static Integer getColumnWidth(String key) {
		return ApplicationData.get().getStateByKey().get(key).getWidth();					
	}
	
	public static Integer getOrder(String key) {
		return ApplicationData.get().getStateByKey().get(key).getOrder();
	}
	
	public static Stream<String> headers(){
		
		Stream<String> result = ApplicationData.get().getStates().stream().sorted((x,y) -> x.getOrder().compareTo(y.getOrder())).map(x -> x.getStateTicket().getName());		
		return result;
	}
	
	public static Stream<State> headersWithWidth(){
		
		Stream<State> result = ApplicationData.get().getStates().stream().sorted((x,y) -> x.getOrder().compareTo(y.getOrder()));		
		return result;
	}
	
}
