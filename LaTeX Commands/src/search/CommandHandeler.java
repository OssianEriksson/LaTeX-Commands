package search;

import java.nio.charset.Charset;
import java.util.List;

public class CommandHandeler {
	
	public static final char[] BREAK_COMMAND = { '.', ',', ':', ';', '(', ')', '/', '+', '-', '<', '>', '=', '^', '_', '\'', '\"', '�', '`', '#', '$', '&',
	'%', '\n', '\t' };
public static final char[] BREAK_FIRST_CHAR = Utils.append(BREAK_COMMAND, '*', '{', '}', '[', ']', '|');
	
	public static void findCommands(String text, PackageName packageName, List<Command> out) {
		String[] parts = text.split("\\\\");

		PackageName textPackageName = new PackageName(null, packageName);

		for (String s : parts) {
			if (s.length() == 0) {
				continue;
			}

			int squareBracketDepth = 0;
			int curlyBracketDepth = 0;
			int commandEndIndex = -1;
			byte[] brackets = new byte[0];

			if (Character.isWhitespace(s.charAt(0)) || Utils.contains(BREAK_FIRST_CHAR, s.charAt(0))) {
				commandEndIndex = 1;
			}
			for (int i = 1; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c == '{') {
					if (commandEndIndex < 0) {
						commandEndIndex = i;
					}
					if (curlyBracketDepth == 0) {
						brackets = Utils.append(brackets, Command.CURLY_BRACKET);
					}
					curlyBracketDepth++;
				} else if (c == '}') {
					curlyBracketDepth--;
				} else if (c == '[') {
					if (commandEndIndex < 0) {
						commandEndIndex = i;
					}
					if (squareBracketDepth == 0) {
						brackets = Utils.append(brackets, Command.SQUARE_BRACKET);
					}
					squareBracketDepth++;
				} else if (c == ']') {
					squareBracketDepth--;
				}
				if (Character.isWhitespace(c) || Utils.contains(BREAK_COMMAND, c) || squareBracketDepth < 0 || curlyBracketDepth < 0) {
					if (commandEndIndex < 0) {
						commandEndIndex = i;
					}
					break;
				}
			}
			if (commandEndIndex < 0) {
				commandEndIndex = s.length();
			}
			String name = s.substring(0, commandEndIndex);

			if (!Charset.forName("US-ASCII").newEncoder().canEncode(name)) {
				continue;
			}

			boolean starrable = false;
			if (name.endsWith("*") && name.length() > 1) {
				starrable = true;
				name = name.substring(0, name.length() - 1);
			}

			if (name.equals("ProvidesPackage")) {
				String[] p = s.split("\\{", 2);
				if (p.length > 1) {
					String pName = p[1].split("\\}")[0];

					if (pName.length() > 0) {
						synchronized (packageName) {
							packageName.setName(pName);
							textPackageName.setName(pName);
						}
					}
				}
			}

			CommandHandeler.addCommand(new Command(name, textPackageName, starrable, brackets), out);
		}
	}

	public static void addCommand(Command command, List<Command> list) {
		int i = 0;

		Command merged = null;

		for (Command c : list) {
			int diff = Utils.compare(c.getName(), command.getName());
			if (diff == 0) {
				String mergedName = c.getName();
				PackageName mergedPackage = c.getPackageName();
				boolean mergedStarrable = c.isStarrable() || command.isStarrable();

				int addCurly;
				int addSquare;
				byte[] mergedBrackets;
				if (c.getBrackets().length > command.getBrackets().length) {
					mergedBrackets = c.getBrackets();
					addCurly = command.getCurlyBracketCount() - c.getCurlyBracketCount();
					addSquare = command.getSquareBracketCount() - c.getSquareBracketCount();
				} else {
					mergedBrackets = command.getBrackets();
					addCurly = c.getCurlyBracketCount() - command.getCurlyBracketCount();
					addSquare = c.getSquareBracketCount() - command.getSquareBracketCount();
				}

				if (addCurly > 0) {
					byte[] append = new byte[addCurly];
					for (int j = 0; j < append.length; j++) {
						append[j] = Command.CURLY_BRACKET;
					}
					mergedBrackets = Utils.append(mergedBrackets, append);
				} else if (addSquare > 0) {
					byte[] append = new byte[addSquare];
					for (int j = 0; j < append.length; j++) {
						append[j] = Command.SQUARE_BRACKET;
					}
					mergedBrackets = Utils.append(append, mergedBrackets);
				}

				merged = new Command(mergedName, mergedPackage, mergedStarrable, mergedBrackets);
				break;
			} else if (diff > 0) {
				break;
			}
			i++;
		}

		if (merged == null) {
			list.add(i, command);
		} else {
			list.remove(i);
			list.add(i, merged);
		}
	}
	
}
