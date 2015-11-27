package kanban.utils.callback;

import java.util.function.Consumer;

public class Then<T> {
	
	
	private Consumer<T> consumer;
	
	public void when(Consumer<T> callback){
		this.consumer = callback;
	}

	public void apply(T value) {
		this.consumer.accept(value);
		
	}
}
