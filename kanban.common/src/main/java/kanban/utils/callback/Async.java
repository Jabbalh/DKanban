package kanban.utils.callback;

public class Async {

	public static <T> Then<T> When(RunnableFunction<MongoCallBack<T>> toDo){		
		return new Then<>(toDo);
	}
}
