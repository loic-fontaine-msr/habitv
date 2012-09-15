package ted;

/****************************************************
 * IMPORTS
 ****************************************************/
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Vector;

import ted.log.ITedLogEntry;
import ted.log.LogEntryFactory;
import ted.log.SimpleLogEntry;
import ted.log.TorrentLogEntry;
import ted.log.LogEntryFactory.InvalidLogEntryType;

/**
 * TED: Torrent Episode Downloader (2005 - 2006)
 *
 * This is the way to add log messages to the TedLog log viewer
 *
 * @author mlathe
 *
 * ted License:
 * This file is part of ted. ted and all of it's parts are licensed
 * under GNU General Public License (GPL) version 2.0
 *
 * for more details see: http://en.wikipedia.org/wiki/GNU_General_Public_License
 *
 */
public class TedLog
{
	/****************************************************
	 * GLOBAL VARIABLES
	 ****************************************************/
	private static final long serialVersionUID = -8661705723352441097L;
    public static final int DEBUG_MESSAGE = 0;
    public static final int ERROR_MESSAGE = 1;
    public static final int SIMPLE_MESSAGE = 2;
    public static final int TORRENT_MESSAGE = 3;

	public static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.DEFAULT);
	public static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);

    private boolean writeToFile;
    public static String LOG_FILE = TedSystemInfo.getUserDirectory() + "log.xml";
	private File logFile = new File(LOG_FILE);
	private PrintWriter fileWriter;
	private final Vector<ITedLogEntry> log = new Vector<ITedLogEntry>();
    private static TedLog logSingleton = null;

	/****************************************************
	 * CONSTRUCTORS
	 ****************************************************/
    /**
	 * Constructs a TedLogger
	 */
	private TedLog()
	{
		resetLogFile();
	}

	// Handle multi threading problems. Only allow one singleton to be made.
    private synchronized static void createInstance()
    {
        if (logSingleton == null)
        {
        	logSingleton = new TedLog();
        }
    }

    public static TedLog getInstance()
    {
        if (logSingleton == null)
        {
        	createInstance();
        }

        return logSingleton;
    }

    // Prevent cloning.
    public Object clone() throws CloneNotSupportedException
    {
    	throw new CloneNotSupportedException();
    }
	/****************************************************
	 * PUBLIC METHODS
	 ****************************************************/
    public void torrent(String show, int seeders, int leechers, String feed, String torrent, String url){
		LogEntryFactory f = new LogEntryFactory();
		ITedLogEntry entry = f.buildTorrentEntry(show, seeders, leechers, feed, torrent, url);

		addEntry(entry);

    }
    /**
     * Add a debug message
     * @param s String to be added
     */
    public void debug(String s)
    {
    	buildEntry(DEBUG_MESSAGE, s);
    }

    /**
     * Add a error message
     * @param s String to be added
     */
    public void error(String s)
    {
    	buildEntry(ERROR_MESSAGE, s);
    }

    /**
     * Add a error message
     * @param s String to be added
     */
    public void error(Exception e, String s)
    {
    	buildEntry(ERROR_MESSAGE, s + " Exception message=[" + e.getMessage() + "]");
    }

    public void simpleLog(String s)
    {
    	buildEntry(SIMPLE_MESSAGE, s);
    }

	public void setWriteToFile(boolean b)
	{
		writeToFile = b;
	}

	public boolean isWriteToFile()
	{
		return writeToFile;
	}

	public void clearLog(){
		log.clear();
	}
	public Vector<ITedLogEntry> getAllEntries(){
		return log;
	}
	public Vector<ITedLogEntry> getSimpleEntries(){
		Vector<ITedLogEntry> entries = new Vector<ITedLogEntry>();
		for(ITedLogEntry entry: log){
			if(entry instanceof SimpleLogEntry || entry instanceof TorrentLogEntry)
				entries.add(entry);
		}
		return entries;
	}


	/****************************************************
	 * PRIVATE METHODS
	 ****************************************************/
	private void addEntry(ITedLogEntry entry){

		while(log.size() >= TedConfig.getInstance().getMaxLogLines()){
			log.remove(0);
		}
		log.add(entry);
	}
	/**
	 * Add an entry to the log
	 * @param s String to be added
     * @param level What log level this entry should be added at
	 */
	private void buildEntry(int level, String s)
	{

		if(TedConfig.getInstance().isAllowLogging())
		{
			try{
				LogEntryFactory factory = new LogEntryFactory();
				ITedLogEntry entry = factory.buildLogEntry(level, s);

				addEntry(entry);

		        if(writeToFile)
		        	addEntryToFile(entry.toString());
	        }catch(InvalidLogEntryType e){
	        	error("Invalid Level " + level + " used for message " + s );
	        }
		}
	}
    /**
     *
     */
	private void resetLogFile()
	{
		// delete current logfile
		logFile.delete();
		openLogFileWriter();
	}
    /**
     * open the log filewriter
     */
    private void openLogFileWriter(){
		try
		{
			logFile.createNewFile();
			fileWriter =  new PrintWriter(logFile);
			setWriteToFile(true);
		}
		catch (IOException e)
		{
			TedLog.getInstance().error(e, "Error opening logfile");
			setWriteToFile(false);
		}
    }
	/**
	 * Write one line to the logfile
	 * @param line The line to write to the logfile
	 * @throws IOException
	 */
	private void addEntryToFile(String line)
	{
		try{
			// append line to logfile
			fileWriter.write(line);
			fileWriter.flush();
		}catch(Exception e){
			System.out.println("Error writing logfile");
		}
	}
}
