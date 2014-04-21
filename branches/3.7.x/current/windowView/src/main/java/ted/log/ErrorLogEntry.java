package ted.log;

import java.util.Date;
import java.util.GregorianCalendar;

import ted.Lang;
import ted.TedLog;

public class ErrorLogEntry implements ITedLogEntry {

	private final String text;
	private final Date datetime = GregorianCalendar.getInstance().getTime();

	protected ErrorLogEntry(String text){
		this.text = text;
	}

	public String toString() {
		String displayText =
			Lang.getString("TedLog.Error")+ " - "
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
