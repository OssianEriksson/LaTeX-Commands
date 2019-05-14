package search;

public class PackageName {

	private final PackageName parent;
	private String name;
	private final String url;

	public PackageName(String url, PackageName parent) {
		this.url = url;
		this.parent = parent;
		
		name = null;
	}
	
	public PackageName(String url, String name) {
		this.url = url;
		this.name = name;
		
		parent = null;
	}

	public PackageName getParent() {
		return parent;
	}

	public String getName() {
		if (name == null) {
			return parent.getName();
		}
		return name;
	}

	public String getUrl() {
		return url;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean hasFixedName() {
		return name != null;
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
