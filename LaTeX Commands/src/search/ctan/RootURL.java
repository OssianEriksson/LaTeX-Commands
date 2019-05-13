package search.ctan;

public class RootURL {

	private final String url;
	private final int folderNamePos;
	private final String[] blacklist;

	public RootURL(String url, int folderNamePos, String[] blacklist) {
		this.url = url;
		this.folderNamePos = folderNamePos;
		this.blacklist = blacklist;
	}

	public String getUrl() {
		return url;
	}

	public int getFolderNamePos() {
		return folderNamePos;
	}

	public String[] getBlacklist() {
		return blacklist;
	}

}
