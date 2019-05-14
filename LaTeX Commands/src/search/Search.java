package search;

import java.util.ArrayList;
import java.util.List;

import search.comprehensive_symbols.SymbolsCommands;
import search.ctan.CTANCommands;
import search.detexify.DetexifyCommands;
import search.files.FilesCommands;

public class Search {

	public static void main(String[] args) {
		List<Command> commands = new ArrayList<Command>();
		FilesCommands.searchCommands(commands);
		DetexifyCommands.searchCommands(commands);
		SymbolsCommands.searchCommands(commands);
		CTANCommands.searchCommands(commands);
		for (Command c : commands) {
			System.out.println(c);
		}
		
		System.out.println("\nTotal: " + commands.size());
	}

}
