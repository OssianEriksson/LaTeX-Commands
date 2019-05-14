package search.ctan;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import search.Utils;

public class UpdateURLList {

	public static final String[] FILE_EXTENSIONS = { ".sti", ".sty", ".def" };
	public static final String OUTPUT = "/chalmers/users/ossiane/Documents/Java/LaTeX/V2";
	public static RootURL[] ROOT = {
			new RootURL("http://ftp.acc.umu.se/mirror/CTAN/", 5, new String[] { "http://ftp.acc.umu.se/mirror/CTAN/documentation/" }) };
	public static final int THREADS = 32;

	public static void main(String[] args) {
		File dir = new File(OUTPUT);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		for (RootURL root : ROOT) {
			processRoot(root);
		}
	}

	public static void processRoot(RootURL root) {
		ExecutorService pool = Executors.newFixedThreadPool(THREADS);

		try {
			Elements elements = Jsoup.connect(root.getUrl()).ignoreContentType(true).get().select("a[href]");

			List<String> visited = new ArrayList<String>();
			visited.add(Utils.getMainURL(root.getUrl()));

			for (Element a : elements) {
				String url = a.attr("abs:href");
				String mainUrl = Utils.getMainURL(url);

				if (visited.contains(mainUrl) || !url.startsWith(root.getUrl()) || !mainUrl.endsWith("/")
						|| Utils.startsWithAny(root.getUrl(), root.getBlacklist())
						|| !url.startsWith("http://ftp.acc.umu.se/mirror/CTAN/fonts/")) {
					continue;
				}

				visited.add(mainUrl);

				pool.execute(new Runnable() {

					@Override
					public void run() {
						String folderName = Utils.getFolderName(url, root.getFolderNamePos());

						System.out.println("Starting: " + url);

						List<String> output = crawl(url);

						if (output.size() == 0) {
							return;
						}

						try {
							Files.write(Paths.get(OUTPUT + "/" + folderName + ".txt"), output, Charset.forName("UTF-8"));
							System.out.println("Done: " + url);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		pool.shutdown();
	}

	public static List<String> crawl(String root) {
		List<String> out = new LinkedList<String>();
		crawl(root, new String[] { root }, FILE_EXTENSIONS, out, new ArrayList<String>());
		return out;
	}

	private static void crawl(String root, String[] prefixes, String[] fileExtensions, List<String> out, List<String> visited) {
		String mainUrl = Utils.getMainURL(root);
		if (!Utils.startsWithAny(root, prefixes) || visited.contains(mainUrl)) {
			return;
		}

		visited.add(mainUrl);

		if (Utils.endsWithAny(mainUrl, fileExtensions)) {
			out.add(root);
		} else {
			Elements elements;
			try {
				elements = Jsoup.connect(root).ignoreContentType(true).get().select("a[href]");

				for (Element a : elements) {
					crawl(a.attr("abs:href"), prefixes, fileExtensions, out, visited);
				}
			} catch (IOException e) {
				return;
			}
		}
	}
}
