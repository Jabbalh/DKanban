package kanban.utils.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import kanban.utils.log.Logger;

public class Tools {

	private static Logger logger = Logger.Get(Tools.class);
	
	public static Date parseDate(String date){
		try {
			Date result =  sdf.parse(date);
			logger.info(() -> "parseDate -> " +sdf.format(result));
			return result;
		} catch (ParseException e) {
			logger.error(() -> "Parse date error " + e.getMessage(),e);
			return null;			
		}
	}
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
}
