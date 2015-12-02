package kanban.web.services;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Session;
import kanban.entity.db.User;
import kanban.entity.session.SessionData;

public class SessionService implements ISessionService {

	private static String JWT_PREFIXE = "Bearer";
	private static String SESSION_DATA = "SESSION_DATA";	
	
	/**
	 * Vérification si l'utilisateur est authentifié
	 */
	@Override
	public Boolean isAuthenticate(Session session,HttpServerRequest request){			
		if (getSessionData(session).getCurrentUser() != null){				
			SessionData sessionData = getSessionData(session);				
			MultiMap mapHeader = request.headers();
			String token = mapHeader.get("Authorization");
			
			return token.equals(fullToken(sessionData.getToken()));
		}
		
		return false;
	}
	
	@Override
	public User getCurrentUser(Session session){
		return getSessionData(session).getCurrentUser();
	}
	
	/**
	 * Ajout de l'objet application dans la session
	 */
	@Override
	public void toSession(Session session,SessionData sessionData){
		session.put(SESSION_DATA, sessionData);	
	}
	
	/**
	 * Renvois le Token avec le suffixe (pour comparer le header HTTP)
	 * @param token
	 * @return
	 */
	private String fullToken(String token){
		return String.format("%s %s", JWT_PREFIXE,token);
	}
		
	
	/**
	 * Renvois l'objet session casté
	 * @param session
	 * @return
	 */
	public static SessionData getSessionData(Session session) {
		SessionData sessionData = null;
		
		if (session.get(SESSION_DATA) == null){
			sessionData = new SessionData();
			session.put(SESSION_DATA, sessionData);			
		} else {
			sessionData = (SessionData) session.get(SESSION_DATA);					
		}
		
		return sessionData;
	}
	
}
