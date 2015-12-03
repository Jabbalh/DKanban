package kanban.utils.callback;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Then<T> {
		
	private List<Function<T, Boolean>> rules = new LinkedList<>();
	private Consumer<T> consumer;
	private Consumer<T> otherwise;
	
	public static Function<?,Boolean> RuleNotNull = x -> x != null;
	
	
	public Then(){
		
	}
	
	public static <T> Boolean NotNull(T value){
		return value != null;
	}
	
	public Then<T> Rule(Function<T, Boolean> rule){
		this.rules.add(rule);
		return this;
	}
	
	public Then<T> Otherwise(Consumer<T> other){
		this.otherwise = other;
		return this;
	}
	
	public Then<T> when(Consumer<T> callback){
		this.consumer = callback;
		return new Then<T>();
	}

	public void apply(T value) {
		if (this.consumer != null){
			boolean ruleOk = true;
			for (Function<T, Boolean> rule : rules){
				ruleOk = rule.apply(value);
				if (!ruleOk) break;
			}
			
			if (!ruleOk && otherwise != null){
				otherwise.accept(value);
			} else {			
				this.consumer.accept(value);
			}
		}				
	}
	
	
	
}
