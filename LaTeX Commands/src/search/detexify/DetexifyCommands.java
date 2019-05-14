package search.detexify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;

import search.Command;
import search.CommandHandeler;
import search.PackageName;

public class DetexifyCommands {

	public static final String URL = "https://raw.githubusercontent.com/kirel/detexify/master/lib/latex/symbols.yaml";

	public static void main(String[] args) {
		List<Command> commands = new ArrayList<Command>();
		searchCommands(commands);

		for (Command c : commands) {
			System.out.println(c);
		}
	}

	public static void searchCommands(List<Command> commands) {
		String text;
		try {
			text = Jsoup.connect(URL).ignoreContentType(true).get().text();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		String[] packageParts = text.split("- package: ");

		for (int i = 0; i < packageParts.length; i++) {
			String packageName;
			if (i == 0) {
				packageName = "LaTeX 2e";
			} else {
				int spaceIndex = packageParts[i].indexOf(" ");
				if (spaceIndex < 0) {
					packageName = "default";
				} else {
					packageName = packageParts[i].substring(0, spaceIndex + 1);
				}
			}

			CommandHandeler.findCommands(packageParts[i], new PackageName(null, packageName), commands);
		}
		
		System.out.println("(Detexify) Done");
	}

}
