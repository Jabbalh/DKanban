package fr.kanban.front;

import java.util.Map;

import io.vertx.ext.web.Session;
import kanban.entity.session.SessionData;

public class UiConstantes {
	
	public static String SESSION_DATA = "SESSION_DATA";
	public static String DKANBAN = "DKANBAN";
	/**
	 * Renvois l'objet session casté
	 * @param session
	 * @return
	 */
	public static SessionData getSessionData(Session session) {
		SessionData sessionData = null;
		Map<String,Object> data = session.data();
		if (data.get(SESSION_DATA) != null){							
			sessionData = (SessionData) data.get(SESSION_DATA); 
		} else {
			sessionData = new SessionData();
			data.put(SESSION_DATA, sessionData);			
		}
		return sessionData;
	}
	
}
