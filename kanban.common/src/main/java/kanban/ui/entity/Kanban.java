package kanban.ui.entity;

import java.util.List;

public class Kanban {
	public KanbanFirstColumn firstColumn;
	public List<KanbanColumn> columns;
	public KanbanFirstColumn getFirstColumn() {
		return firstColumn;
	}
	public void setFirstColumn(KanbanFirstColumn firstColumn) {
		this.firstColumn = firstColumn;
	}
	public List<KanbanColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<KanbanColumn> columns) {
		this.columns = columns;
	}
}
