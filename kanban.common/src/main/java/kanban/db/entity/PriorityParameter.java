package kanban.db.entity;

import java.util.function.Consumer;

public class PriorityParameter extends AbstractParameter implements IColor {
private String color;
	
	public PriorityParameter() {
		this.setKanbanParameter(KanbanParameter.EXPOSED_ID);
	}
	
	
	
	public PriorityParameter(String code, String libelle) {
		super(KanbanParameter.EXPOSED_ID,code,libelle);		
	}



	public PriorityParameter(Consumer<PriorityParameter> cstr){
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
		PriorityParameter cast = (PriorityParameter)clone;
		this.setColor(cast.getColor());
	}
}
