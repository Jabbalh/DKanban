package kanban.utils.callback;

public class MongoCallBack<T> {

	
	private FinishListener<T> finishEvent;
	
	private T value = null;
	
	public void finish(T value){
		if (finishEvent == null){
			this.value = value;
		} else {
			finishEvent.finishEvent(value);
		}
		
	}
	
	
	
	
	public void finishHandler(FinishListener<T> run){
		finishEvent = run;
		if (value != null){
			finishEvent.finishEvent(value);
		}
	}
}
