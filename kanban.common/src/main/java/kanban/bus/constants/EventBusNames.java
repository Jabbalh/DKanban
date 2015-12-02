package kanban.bus.constants;

public class EventBusNames {

	/**
	 * BUS relatif à la construction du Kanban
	 */
	public final static String KANBAN_TICKET_BY_USER = "KANBAN_TICKET_BY_USER";
	public final static String KANBAN_HEADER_LIST = "KANBAN_HEADER_LIST";
	public final static String KANBAN_BY_USER = "KANBAN_BY_USER";
	
	/**
	 * BUS relatif aux tickets
	 */
	
	public final static String TICKET_LIST = "TICKET_LIST";
	public final static String TICKET_UPDATE_STATE = "TICKET_UPDATE_STATE";
	public final static String TICKET_UPDATE_ALL = "TICKET_UPDATE_ALL";
	public final static String TICKET_INSERT_ALL = "TICKET_INSERT_ALL";
	public final static String TICKET_ARCHIVE = "TICKET_ARCHIVE";
	/**
	 * BUS relatif aux utilisateurs
	 */
	public final static String USER_LIST = "USER_LIST";
	public final static String USER_FIND_BY_LOGIN = "USER_FIND_BY_LOGIN";
	public final static String USER_AUTHENTICATE = "USER_AUTHENTICATE";
	
	/**
	 * BUS relatif aux applications
	 */
	public final static String APPLICATION_LIST = "APPLICATION_LIST";
	public final static String STATE_LIST = "STATE_LIST";
	
}
