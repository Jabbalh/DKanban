package kanban.db.entity;

public class ParamTuple {

	private String code;
	private String libelle;
	
	public ParamTuple(){
		
	}
	
	public ParamTuple(String code, String libelle) {
		super();
		this.code = code;
		this.libelle = libelle;
	}
	
	public <T extends AbstractParameter> ParamTuple(T value){
		this(value.getCode(), value.getLibelle());				
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	
	public static ParamTuple from(User user){
		return new ParamTuple(user.getLogin(),user.getFirstName() + " " + user.getLastName());
	}
	
	public static ParamTuple from(ZoneParameter zone){
		return new ParamTuple(zone.getCode(), zone.getCode());
	}
	
	public static ParamColorTuple from(StatutParameter statut){
		return new ParamColorTuple(statut.getCode(),statut.getLibelle(),statut.getColor());		
	}
	
	public static ParamColorTuple from(PriorityParameter priority){
		return new ParamColorTuple(priority.getCode(),priority.getLibelle(),priority.getColor());		
	}
	
	public static ParamTuple from(ApplicationParameter app){
		return new ParamTuple(app.getCode(), app.getCode());
	}
	
	public int safeCompare(ParamTuple other){
		if (this.getLibelle() != null && other.getLibelle() != null){
			return this.getLibelle().compareToIgnoreCase(other.getLibelle());
		}
		return 0;
	}
}
