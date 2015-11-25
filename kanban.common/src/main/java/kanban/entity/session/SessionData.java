package kanban.entity.session;

import kanban.entity.db.User;

public class SessionData {
	private User currentUser;

	
	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
}
