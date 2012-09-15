package ted.log;

import java.util.Date;
import java.util.GregorianCalendar;

import ted.Lang;
import ted.TedLog;

public class TorrentLogEntry implements ITedLogEntry {

	private final String show;
    private final int seeders;
    private final int leechers;
    private final String feed;
    private final String torrent;
    private final String url;
	private final Date datetime = GregorianCalendar.getInstance().getTime();

	protected TorrentLogEntry(String show, int seeders, int leechers, String feed,
			String torrent, String url) {
		super();
		this.show = show;
		this.seeders = seeders;
		this.leechers = leechers;
		this.feed = feed;
		this.torrent = torrent;
		this.url = url;
	}

	public String toString() {
		String displayText =
			  TedLog.DATE_FORMAT.format(datetime) + " @ "
			+ TedLog.TIME_FORMAT.format(datetime) + ": "
		    + Lang.getString("TedLog.Found") + " "
			+ Lang.getString("TedLog.Show") + " " + show + " "
			+ Lang.getString("TedLog.Feed") + " " + feed + " "
			+ Lang.getString("TedLog.Torrent") + " " + torrent + " "
			+ Lang.getString("TedLog.Seeders") + " " + seeders + " "
			+ Lang.getString("TedLog.Leechers") + " "+  leechers;
		return displayText;
	}
	public Date getDateTime() {
		return datetime;
	}

	public String getURL(){
		return url;
	}
	public String getTorrent(){
		return torrent;
	}

}
