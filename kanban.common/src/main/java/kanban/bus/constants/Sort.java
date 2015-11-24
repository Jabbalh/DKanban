package kanban.bus.constants;

public enum Sort {
	ASC(1), DESC(-1);
	
	private Integer value;
	
	Sort(Integer value){
		this.value = value;
	}
	
	public Integer value(){
		return this.value;
	}
}
