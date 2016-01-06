package kanban.db.entity;

public class ParamColorTuple extends ParamTuple {

	public String color;

	public ParamColorTuple(){
		super();
	}
	
	
	
	
	public <T extends AbstractParameter & IColor> ParamColorTuple(T value){
		this(value.getCode(), value.getLibelle());
		this.setColor(value.getColor());
		
	}
	
	public ParamColorTuple(String string, String string2) {
		super(string,string2);
	}
	
	public ParamColorTuple(String code, String libelle,String color) {
		super(code,libelle);
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
