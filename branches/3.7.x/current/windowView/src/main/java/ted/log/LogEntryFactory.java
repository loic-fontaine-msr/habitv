package ted.log;

import ted.TedLog;

public class LogEntryFactory {

	public class InvalidLogEntryType extends Exception{
		private static final long serialVersionUID = 1L;
	}

	public ITedLogEntry buildLogEntry(int type, String message) throws InvalidLogEntryType{
		ITedLogEntry entry;
		switch(type){
			case TedLog.DEBUG_MESSAGE:
				entry = new DebugLogEntry(message);
			break;
			case TedLog.ERROR_MESSAGE:
				entry = new ErrorLogEntry(message);
			break;
			case TedLog.SIMPLE_MESSAGE:
				entry = new SimpleLogEntry(message);
			break;
			default:
				throw new InvalidLogEntryType();
		}
		return entry;
	}

	public ITedLogEntry buildTorrentEntry(String show, int seeders, int leechers, String feed,
			String torrent, String url){
		return new TorrentLogEntry(show, seeders, leechers, feed, torrent, url);
	}
}
