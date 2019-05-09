package search;

public class Command {
	
	public static final short SQUARE_BRACKET = 1, CURLY_BRACKET = 2;

	private final String name, packageName;
	private final boolean starrable;
	private final short squareBracketCount, curlyBracketCount;
	private final short[] brackets;

	public Command(String name, String packageName, boolean starrable, short[] brackets) {
		this.name = name;
		this.packageName = packageName;
		this.starrable = starrable;
		this.brackets = brackets;
		
		short squareBracketCount = 0;
		short curlyBracketCount = 0;
		
		for (short s : brackets) {
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

	public short getSquareBracketCount() {
		return squareBracketCount;
	}

	public short getCurlyBracketCount() {
		return curlyBracketCount;
	}

	public short[] getBrackets() {
		return brackets;
	}
	
	@Override
	public String toString() {
		String out = name + (starrable ? "*" : "");
		for (short s : brackets) {
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
			for (short s : brackets) {
				out += s + ",";
			}
			return out.substring(0, out.length() - 1);
		}
		return out;
	}
}
