package kanban.db.entity;

import java.util.function.Consumer;

/**
 * Représente le statut d'un ticket
 *
 */
public class StatutParameter extends AbstractParameter implements IColor  {
	
	private String color;
	
	public StatutParameter() {
		this.setKanbanParameter(KanbanParameter.EXPOSED_ID);
	}
		
	
	public StatutParameter(String code, String libelle) {
		super(KanbanParameter.EXPOSED_ID,code,libelle);		
	}



	public StatutParameter(Consumer<StatutParameter> cstr){
		this();
		cstr.accept(this);
	}

	public String getColor() {
		return color;
	}



	public void setColor(String color) {
		this.color = color;
	}
	
	@Override
	public void clone(AbstractParameter clone){
		super.clone(clone);
		StatutParameter cast = (StatutParameter)clone;
		this.setColor(cast.getColor());
	}


	
	
}
