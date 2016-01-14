package kanban.utils.callback;

@FunctionalInterface
public interface RunnableFunction<T> {
	T apply();
}
