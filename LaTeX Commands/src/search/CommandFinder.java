package search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jsoup.Jsoup;

public class CommandFinder {

	public static final char[] BREAK_COMMAND = {'.', ',', ':', ';', '(', ')', '/', '+', '-', '<', '>', '^', '_'};
	public static final String[] INPUT = { "/chalmers/users/ossiane/Documents/Java/LaTeX/V2" };

	public static void main(String[] args) {
		List<Command> commands = new ArrayList<Command>();
		for (String path : INPUT) {
			findCommands(new File(path), commands);
		}
	}

	public static void findCommands(File file, List<Command> out) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				findCommands(f, out);
			}
		} else if (file.canRead()) {
			Scanner scanner = null;

			try {
				scanner = new Scanner(file);
				
				while (scanner.hasNextLine()) {
					findCommands(Jsoup.connect(scanner.nextLine().trim()).ignoreContentType(true).get().text(), out);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (scanner != null) {
					scanner.close();
				}
			}
		}
	}
	
	public static void findCommands(String text, List<Command> out) {
		String[] parts = text.split("\\\\");
		
		for (String s : parts) {
			int squareBracketDepth = 0;
			int curlyBracketDepth = 0;
			int squareBracketCount = 0;
			int curlyBracketCount = 0;
			
			for (int i = 1; i < s.length(); i++) {
				char c = s.charAt(i);
				if (Character.isWhitespace(c)) {
					
				}
			}
		}
	}

}
