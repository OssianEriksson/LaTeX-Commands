package search;

import java.util.HashMap;
import java.util.Map;

public class PackageNameList {

	private final Map<String, PackageName> names;
	private final String defaultName;

	public PackageNameList(String defaultName) {
		this.defaultName = defaultName;
		names = new HashMap<String, PackageName>();
	}

	public PackageName getPackageName(String url) {
		PackageName n = names.get(url);
		if (n == null) {
			int lastSlash = url.lastIndexOf('/');
			if (lastSlash < 0) {
				n = new PackageName(url, defaultName);
			} else {
				n = new PackageName(url, getPackageName(url.substring(0, lastSlash)));
			}
			names.put(url, n);
		}
		return n;
	}
}
