package kanban.entity.session;

import kanban.db.entity.KanbanParameter;

public class ApplicationData {

	private static KanbanParameter kanban;
	public static Boolean isInit = false;
	public static KanbanParameter get(){
		return kanban;
	}
	
	public static void set(KanbanParameter k){
		kanban = k;		
	}
}
