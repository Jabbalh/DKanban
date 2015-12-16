package kanban.utils.callback;

import java.util.EventListener;

public interface FinishListener<T> extends EventListener {
	void finishEvent(T value);
}
