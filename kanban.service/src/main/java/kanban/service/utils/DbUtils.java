package kanban.service.utils;

public class DbUtils {

	public static <T> String index(Class<T> clazz){
		return clazz.getSimpleName();
	}
	
}
