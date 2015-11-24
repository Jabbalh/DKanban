package kanban.entity.db;

import java.util.UUID;

public class Application {
	private String name;
	private String description;
	private String version;
	
	public Application() {
		_id = UUID.randomUUID().toString();
	}
	
	public Application(String name, String description) {
		this();
		this.name = name;
		this.description = description;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	private String _id;
	public String get_id() { return _id;}
	public void set_id(String _id) { this._id = _id;}
}
