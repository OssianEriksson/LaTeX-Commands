package search;

import java.util.ArrayList;
import java.util.List;

public class PackageNameList {

	private final String originalName;
	private final List<String> possibleNames;

	public PackageNameList(String originalName) {
		this.originalName = originalName;
		this.possibleNames = new ArrayList<String>();
	}

	public String getOriginalName() {
		return originalName;
	}

	public List<String> getPossibleNames() {
		return possibleNames;
	}

	public void addPossibleName(String name) {
		if (!possibleNames.contains(name)) {
			possibleNames.add(name);
		}
	}
}
