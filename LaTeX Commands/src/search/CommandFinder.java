package search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;

public class CommandFinder {

	public static final char[] BREAK_COMMAND = { '.', ',', ':', ';', '(', ')', '/', '+', '-', '<', '>', '=', '^', '_', '\'', '\"', '�', '`', '#', '$', '&',
			'%' };
	public static final char[] BREAK_FIRST_CHAR = Utils.append(BREAK_COMMAND, '*', '{', '}', '[', ']');
	public static final String[] INPUT;
	public static final int PACKAGE_NAME_INDEX = 6, THREADS = 7;

	private static final ExecutorService pool = Executors.newFixedThreadPool(THREADS);

	static {
		if (System.getProperty("os.name").contains("Windows")) {
			INPUT = new String[] { "C:/Users/Ossian/Documents/LaTeX" };
		} else {
			INPUT = new String[] { "/chalmers/users/ossiane/Documents/Java/LaTeX/V2" };
		}
	}

	public static void main(String[] args) {
		List<String> urlsToSearch = new ArrayList<String>();
		for (String s : INPUT) {
			urlsToSearch.addAll(listURLs(new File(s)));
		}
		final int urlCount = urlsToSearch.size();

		AtomicInteger queueLength = new AtomicInteger(0);

		Stack<List<Command>> queue = new Stack<List<Command>>();

		List<Command> commands = new ArrayList<Command>();
		PackageNameList packageNames = new PackageNameList("default");

		new Thread(new Runnable() {

			@Override
			public void run() {
				int processedPackaged = 0;

				while (processedPackaged == 0 || queueLength.get() > 0) {
					boolean empty;
					synchronized (queue) {
						empty = queue.size() == 0;
					}

					if (empty) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						List<Command> list;
						synchronized (queue) {
							list = queue.pop();
						}

						for (Command c : list) {
							addCommand(c, commands);
						}

						processedPackaged++;

						if (commands.size() > 20000) {
							for (Command c : commands) {
								System.out.println(c);
							}
						}

						System.out.printf("%6d/%d\tCommand cound: %d\n", processedPackaged, urlCount, commands.size());
					}
				}
			}
		}).start();

		findCommands(urlsToSearch, queue, PACKAGE_NAME_INDEX, queueLength, packageNames);

		pool.shutdown();
	}

	public static void findCommands(List<String> urls, Stack<List<Command>> queue, int packageNameIndex, AtomicInteger queueLength,
			PackageNameList packageNames) {
		for (String url : urls) {
			PackageName packageName;
			synchronized (packageNames) {
				packageName = packageNames.getPackageName(Utils.getFolderName(url));
			}

			queueLength.incrementAndGet();
			pool.execute(new Runnable() {

				@Override
				public void run() {
					List<Command> out1 = new ArrayList<Command>();
					try {
						findCommands(Jsoup.connect(url).ignoreContentType(true).get().text(), packageName, out1);

						if (out1.size() > 0) {
							synchronized (queue) {
								queue.push(out1);
							}
						}
					} catch (IOException e) {

					} finally {
						queueLength.decrementAndGet();
					}
				}
			});
		}
	}

	public static void findCommands(String text, PackageName packageName, List<Command> out) {
		String[] parts = text.split("\\\\");

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
			boolean starrable = false;
			if (name.endsWith("*") && name.length() > 1) {
				starrable = true;
				name = name.substring(0, name.length() - 1);
			}

			if (name.equals("ProvidesPackage")) {
				String[] p = s.split("\\{", 2);
				if (p.length > 1) {
					String pName = p[1].split("\\}")[0];

					synchronized (packageName) {
						packageName.setName(pName);
					}
				}
			}

			addCommand(new Command(name, packageName, starrable, brackets), out);
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

	public static List<String> listURLs(File file) {
		List<String> out = new LinkedList<String>();
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				out.addAll(listURLs(f));
			}
		} else if (!file.canRead()) {
			return out;
		} else {
			Scanner scanner = null;
			try {
				scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					String url = scanner.nextLine().trim();
					if (url.length() > 0) {
						out.add(url);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (scanner != null) {
					scanner.close();
				}
			}
		}
		return out;
	}
}
