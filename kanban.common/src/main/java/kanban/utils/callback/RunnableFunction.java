package kanban.utils.callback;

@FunctionalInterface
public interface RunnableFunction<T> {
	public T apply();
}
