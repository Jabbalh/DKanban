package kanban.web.services;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Session;
import kanban.entity.db.User;
import kanban.entity.session.SessionData;

public interface ISessionService {

	Boolean isAuthenticate(Session session, HttpServerRequest request);

	void toSession(Session session, SessionData sessionData);

	User getCurrentUser(Session session);

	void signOut(Session session, HttpServerRequest request);

}
