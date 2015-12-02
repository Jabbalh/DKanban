package kanban.service.utils;

public class BusUtils {

	/**
	 * Test la nullité du retour du bus
	 * @param busResult
	 * @return
	 */
	public static Boolean isNull(Object busResult){
		return (busResult != null && busResult.toString().toLowerCase().equals("null"))
				|| busResult == null;
	}
	
	public static Boolean isNotNull(Object busResult){
		return !isNull(busResult);
	}
	
}
