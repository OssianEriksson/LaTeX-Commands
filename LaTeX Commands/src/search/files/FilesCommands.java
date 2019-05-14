package search.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import search.Command;
import search.CommandHandeler;
import search.PackageName;

public class FilesCommands {

	public static void main(String[] args) {
		List<Command> commands = new ArrayList<Command>();
		searchCommands(commands);

		for (Command c : commands) {
			System.out.println(c);
		}
	}

	public static void searchCommands(List<Command> commands) {
		searchFile("/chalmers/users/ossiane/Documents/Java/LaTeX/Documents/command_summary.txt", "default", commands);
		System.out.println("(Files) Done");
	}

	public static void searchFile(String path, String packageName, List<Command> commands) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(path));

			String text = scanner.useDelimiter("\\Z").next();
			CommandHandeler.findCommands(text, new PackageName(null, packageName), commands);
			
			System.out.println("(Files) Searched: " + new File(path).getName());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

}
