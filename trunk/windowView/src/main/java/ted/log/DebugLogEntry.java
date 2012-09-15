package ted.log;

import java.util.Date;
import java.util.GregorianCalendar;

import ted.Lang;
import ted.TedLog;

public class DebugLogEntry implements ITedLogEntry {

	private final String text;
	private final Date datetime = GregorianCalendar.getInstance().getTime();

	protected DebugLogEntry(String text){
		this.text = text;
	}

	public String toString() {
		String displayText =
			Lang.getString("TedLog.Debug")+ " - "
			+ TedLog.DATE_FORMAT.format(datetime) + " @ "
			+ TedLog.TIME_FORMAT.format(datetime) + ": "
			+ text +
			System.getProperty("line.separator");
		return displayText;
	}
	public Date getDateTime() {
		return datetime;
	}

}
