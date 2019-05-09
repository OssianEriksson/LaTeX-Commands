package search;

public class Utils {

	public static String getMainURL(String url) {
		//test
		return url.split("#")[0].split("\\?|#", 2)[0];
	}

	public static boolean startsWithAny(String a, String[] b) {
		for (String c : b) {
			if (a.startsWith(c)) {
				return true;
			}
		}
		return false;
	}

	public static boolean endsWithAny(String a, String[] b) {
		for (String c : b) {
			if (a.endsWith(c)) {
				return true;
			}
		}
		return false;
	}

	public static String getFolderName(String url, int namePos) {
		String[] parts = url.split("/");
		if (parts.length <= namePos) {
			return "unknown" + Math.round((float) Math.random() * 1000000);
		}
		return parts[namePos];
	}

}
