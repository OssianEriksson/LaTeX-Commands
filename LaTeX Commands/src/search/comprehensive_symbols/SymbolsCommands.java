package search.comprehensive_symbols;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import search.Command;
import search.CommandHandeler;
import search.PackageName;
import search.Utils;

public class SymbolsCommands {

	public static void main(String[] args) {
		List<Command> commands = new ArrayList<Command>();
		searchCommands(commands);

		for (Command c : commands) {
			System.out.println(c);
		}
	}

	public static void searchCommands(List<Command> commands) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("/chalmers/users/ossiane/Documents/Java/LaTeX/Documents/symbols.txt"));

			String[] packageParts = scanner.useDelimiter("\\Z").next().split("\\nTable |Table ");

			for (int i = 1; i < packageParts.length; i++) {
				int start = packageParts[i].indexOf(": ");
				int end = Utils.indexOf(packageParts[i], "\\s", start + 2);
				String packageName = start < 0 || end < 0 ? "default" : packageParts[i].substring(start + 2, end + 1).trim();

				CommandHandeler.findCommands(packageParts[i], new PackageName(null, packageName), commands);
			}

			System.out.println("(Symbols) Done");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

}
