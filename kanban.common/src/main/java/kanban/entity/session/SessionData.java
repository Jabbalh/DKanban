package kanban.entity.session;

import kanban.entity.db.User;

public class SessionData {
	private User currentUser;
	private String token;
	
	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
