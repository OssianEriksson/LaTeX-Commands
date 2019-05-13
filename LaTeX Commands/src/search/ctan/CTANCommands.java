package search.ctan;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;

import search.Command;
import search.CommandHandeler;
import search.PackageName;
import search.PackageNameList;
import search.Search;
import search.Utils;

public class CTANCommands {

	public static final String[] INPUT;
	public static final int PACKAGE_NAME_INDEX = 6, THREADS = 7;

	private static final ExecutorService pool = Executors.newFixedThreadPool(THREADS);

	static {
		if (System.getProperty("os.name").contains("Windows")) {
			INPUT = new String[] { "C:/Users/Ossian/Documents/LaTeX" };
		} else {
			INPUT = new String[] { "/chalmers/users/ossiane/Documents/Java/LaTeX/URL" };
		}
	}

	public static void main(String[] args) {
		List<Command> commands = new ArrayList<Command>();
		searchCommands(commands);

		for (Command c : commands) {
			System.out.println(c);
		}
	}

	public static void searchCommands(List<Command> commands) {
		List<String> urlsToSearch = new ArrayList<String>();
		for (String s : INPUT) {
			urlsToSearch.addAll(listURLs(new File(s)));
		}
		final int urlCount = urlsToSearch.size();

		AtomicInteger queueLength = new AtomicInteger(0);
		AtomicBoolean finished = new AtomicBoolean(false);

		Stack<List<Command>> queue = new Stack<List<Command>>();
		PackageNameList packageNames = new PackageNameList("default");

		new Thread(new Runnable() {

			@Override
			public void run() {
				int processedPackaged = 0;
				boolean empty = true;

				while (processedPackaged == 0 || queueLength.get() > 0 || !empty) {
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
							CommandHandeler.addCommand(c, commands);
						}

						processedPackaged++;

						if (processedPackaged % 200 == 0) {
							System.out.println("(CTAN) Urls processed: " + processedPackaged + "/" + urlCount);
						}
					}
				}
				finished.set(true);
			}
		}).start();

		findCommands(urlsToSearch, queue, PACKAGE_NAME_INDEX, queueLength, packageNames);

		pool.shutdown();

		try {
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (!finished.get()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void findCommands(List<String> urls, Stack<List<Command>> queue, int packageNameIndex, AtomicInteger queueLength,
			PackageNameList packageNames) {
		for (String url : urls) {
			String[] urlParts = url.split("/", packageNameIndex + 2);

			PackageName packageName;
			synchronized (packageNames) {
				packageName = packageNames.getPackageName(Utils.getFolderName(url));
				if (!packageName.hasFixedName() && urlParts.length > packageNameIndex) {
					packageName.setName(urlParts[packageNameIndex]);
				}
			}

			queueLength.incrementAndGet();
			pool.execute(new Runnable() {

				@Override
				public void run() {
					List<Command> out1 = new ArrayList<Command>();
					try {
						CommandHandeler.findCommands(Jsoup.connect(url).ignoreContentType(true).get().text(), packageName, out1);

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
