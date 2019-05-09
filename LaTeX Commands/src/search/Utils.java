package search;

public class Utils {
	
	public static boolean contains(char[] a, char b) {
		for (char c : a) {
			if (c == b) {
				return true;
			}
		}
		return false;
	}
	
	public static char[] append(char[] a, char... b) {
		char[] c = new char[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
	
	public static byte[] append(byte[] a, byte... b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	public static String getMainURL(String url) {
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
	
	public static int compare(String a, String b) {
		for (int i = 0; i < a.length() && i < b.length(); i++) {
			int ai = (int) a.charAt(i);
			int bi = (int) b.charAt(i);
			if (ai != bi) {
				return ai - bi;
			}
		}
		return a.length() - b.length();
	}

}
