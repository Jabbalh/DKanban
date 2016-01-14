package kanban.bus.constants;

public class EventBusNames {

	/**
	 * BUS relatif aux sockets js
	 */
	public static final String UPDATE_CARD = "update-card";
	public static final String INSERT_CARD = "insert-card";
	public static final String DELETE_CARD = "delete-card";
	
	/**
	 * BUS relatif à la construction du Kanban
	 */
	public final static String KANBAN_TICKET_BY_USER = "KANBAN_TICKET_BY_USER";
	public final static String KANBAN_HEADER_LIST = "KANBAN_HEADER_LIST";
	public final static String KANBAN_HEADER_LIST_PRIORITY = "KANBAN_HEADER_LIST_PRIORITY";

	public final static String KANBAN_BY_USER = "KANBAN_BY_USER";
	public final static String KANBAN_BY_USER_FOR_PRIORITY = "KANBAN_BY_USER_FOR_PRIORITY";

	
	/**
	 * BUS relatif aux tickets
	 */
	
	public final static String TICKET_LIST = "TICKET_LIST";
	public final static String TICKET_UPDATE_STATE = "TICKET_UPDATE_STATE";
	public final static String TICKET_UPDATE_ALL = "TICKET_UPDATE_ALL";
	public final static String TICKET_INSERT_ALL = "TICKET_INSERT_ALL";
	public final static String TICKET_ARCHIVE = "TICKET_ARCHIVE";
	public final static String TICKET_SEARCH = "TICKET_SEARCH";
	public final static String TICKET_DELETE = "TICKET_DELETE";
	
	/**
	 * BUS relatif aux utilisateurs
	 */
	public final static String USER_LIST = "USER_LIST";
	public final static String USER_FIND_BY_LOGIN = "USER_FIND_BY_LOGIN";
	public final static String USER_AUTHENTICATE = "USER_AUTHENTICATE";
	public final static String USER_SAVE = "USER_SAVE";
	public final static String USER_INSERT = "USER_INSERT";
	
	
	/**
	 * BUS relatif aux applications
	 */
	public final static String APPLICATION_LIST = "APPLICATION_LIST";
	public final static String APPLICATION_SAVE = "APPLICATION_SAVE";		
	
	public final static String STATE_LIST = "STATE_LIST";
	public final static String STATE_SAVE = "STATE_SAVE";
	public final static String STATE_INSERT = "STATE_INSERT";
	
	
	public final static String ZONE_LIST = "ZONE_LIST";
	public final static String ZONE_SAVE = "ZONE_SAVE";
	
	public final static String PRIORITY_SAVE = "PRIORITY_SAVE";
	public final static String PRIORITY_LIST = "PRIORITY_LIST";
	public final static String PRIORITY_INSERT = "PRIORITY_INSERT";
	
	public final static String KANBAN_FULL = "KANBAN_FULL";
	
	public final static String ADMIN_ZONE_LIST = "ADMIN_ZONE_LIST";
	public final static String ADMIN_PRORITY_LIST = "ADMIN_PRORITY_LIST";
	public final static String ADMIN_USER_LIST = "ADMIN_USER_LIST";
	public final static String ADMIN_APP_LIST = "ADMIN_APP_LIST";
	public final static String ADMIN_STATUT_LIST = "ADMIN_STATUT_LIST";
	
	public final static String ADMIN_DELETE = "ADMIN_DELETE";
	
	
	/**
	 * BUS Global
	 */
	public final static String GLOBAL_TITLE_GET = "GLOBAL_TITLE_GET";
	public final static String GLOBAL_TITLE_SET = "GLOBAL_TITLE_SET";

	public static final String ADMIN_USER_UP_PASSWORD = "ADMIN_USER_UP_PASSWORD";
}
