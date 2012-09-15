package ted.log;

import java.util.Date;
import java.util.GregorianCalendar;

import ted.TedLog;


public class SimpleLogEntry implements ITedLogEntry {

	private final String text;
	private final Date datetime = GregorianCalendar.getInstance().getTime();

	protected SimpleLogEntry(String text){
		this.text = text;
	}

	public String toString() {
		String displayText =
			TedLog.DATE_FORMAT.format(datetime) + " @ "
			+ TedLog.TIME_FORMAT.format(datetime) + ": "
			+ text +
			System.getProperty("line.separator");
		return displayText;
	}
	public Date getDateTime() {
		return datetime;
	}

}
