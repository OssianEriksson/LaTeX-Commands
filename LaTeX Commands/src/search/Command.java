package search;

public class Command {
	
	public static final byte SQUARE_BRACKET = 1, CURLY_BRACKET = 2;

	private final String name, packageName;
	private final boolean starrable;
	private final byte squareBracketCount, curlyBracketCount;
	private final byte[] brackets;

	public Command(String name, String packageName, boolean starrable, byte[] brackets) {
		this.name = name;
		this.packageName = packageName;
		this.starrable = starrable;
		this.brackets = brackets;
		
		byte squareBracketCount = 0;
		byte curlyBracketCount = 0;
		
		for (byte s : brackets) {
			if (s == SQUARE_BRACKET) {
				squareBracketCount++;
			} else if (s == CURLY_BRACKET) {
				curlyBracketCount++;
			}
		}
		
		this.squareBracketCount = squareBracketCount;
		this.curlyBracketCount = curlyBracketCount;
	}

	public String getName() {
		return name;
	}

	public String getPackageName() {
		return packageName;
	}

	public boolean isStarrable() {
		return starrable;
	}

	public byte getSquareBracketCount() {
		return squareBracketCount;
	}

	public byte getCurlyBracketCount() {
		return curlyBracketCount;
	}

	public byte[] getBrackets() {
		return brackets;
	}
	
	@Override
	public String toString() {
		String out = name + (starrable ? "*" : "");
		for (byte s : brackets) {
			if (s == SQUARE_BRACKET) {
				out += "[]";
			} else if (s == CURLY_BRACKET) {
				out += "{}";
			}
		}
		return out + "\t" + packageName;
	}

	public String serialize() {
		String out = name + " " + packageName + " " + (starrable ? "*" : "-");
		if (brackets.length > 0) {
			out += " ";
			for (byte s : brackets) {
				out += s + ",";
			}
			return out.substring(0, out.length() - 1);
		}
		return out;
	}
}
