package kanban.db.entity;

import java.util.function.Consumer;

/**
 * Une application affectée à un ticket
 *
 */
public class ApplicationParameter extends AbstractParameter {

	
	public ApplicationParameter() 
	{
		super();
		this.setKanbanParameter(KanbanParameter.EXPOSED_ID);
	}
	
	public ApplicationParameter(Consumer<ApplicationParameter> cstr) {
		this();
		cstr.accept(this);
	}
	
	
	
	public ApplicationParameter(String code, String libelle) {
		super(KanbanParameter.EXPOSED_ID,code,libelle);
	}

	
	
}
