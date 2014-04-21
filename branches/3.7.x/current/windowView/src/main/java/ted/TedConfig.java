package ted;

import java.awt.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.JFileChooser;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * TED: Torrent Episode Downloader (2005 - 2006)
 *
 * TedConfig stores all the configuration variables of ted
 *
 * @author Roel
 * @author Joost
 *
 *         ted License: This file is part of ted. ted and all of it's parts are
 *         licensed under GNU General Public License (GPL) version 2.0
 *
 *         for more details see:
 *         http://en.wikipedia.org/wiki/GNU_General_Public_License
 *
 */
@XmlRootElement(name = "config")
public class TedConfig

{
	/****************************************************
	 * GLOBAL VARIABLES
	 ****************************************************/

	public static final int NEVER = 0;
	public static final int ASK = 1;
	public static final int ALWAYS = 2;
	public static final int DOWNLOADMINIMUMSEEDERS = 0;
	public static final int DOWNLOADMOSTSEEDERS = 1;
	public static final int DOWNLOADBESTRATIO = 2;
	public static final int SORT_OFF = 0;
	public static final int SORT_NAME = 1;
	public static final int SORT_STATUS = 2;
	public static final int SORT_ASCENDING = 0;
	public static final int SORT_DESCENDING = 1;

	// create some default settings
	@XmlElement(name = "refresh")
	private int RefreshTime = 3600;
	@XmlElement(name = "directory")
	private String Directory = "";
	@XmlElement(name = "showerrors")
	private boolean ShowErrors = false;
	@XmlElement(name = "showhurray")
	private boolean ShowHurray = true;
	@XmlElement(name = "opentorrent")
	private boolean OpenTorrent = true;
	@XmlElement(name = "downloadToShowFolders")
	private boolean downloadToShowFolders = false;
	@XmlElement(name = "downloadToSeasonFolders")
	private boolean downloadToSeasonFolders = false;
	@XmlElement(name = "startminimized")
	private boolean StartMinimized = false;
	@XmlElement(name = "checkversion")
	private boolean CheckVersion = true;
	// TODO JM WTF is downloadNewSeason???
	private boolean downloadNewSeason = true;
	@XmlElement(name = "parse_at_start")
	private boolean parseAtStart = true;
	@XmlElement(name = "rssupdate")
	private int autoUpdateFeedList = ALWAYS;
	@XmlElement(name = "rssadjust")
	private int autoAdjustFeeds = ALWAYS;
	@XmlElement(name = "windowwidth")
	private int width = 400;
	@XmlElement(name = "windowheight")
	private int height = 550;
	@XmlElement(name = "windowx")
	private int x = 0;
	@XmlElement(name = "windowy")
	private int y = 0;
	@XmlElement(name = "rssversion")
	private int RSSVersion = 0;
	@XmlElement(name = "timeoutsecs")
	private int TimeOutInSecs = 10;
	@XmlElement(name = "seedersetting")
	private int SeederSetting = DOWNLOADMOSTSEEDERS;
	@XmlElement(name = "locale_language")
	private String locale_language = "en";
	@XmlElement(name = "locale_country")
	private String locale_country = "US";
	@XmlElement(name = "add_tray")
	private boolean addSysTray = TedSystemInfo.osSupportsJDICTray();
	@XmlElement(name = "downloadcompressed")
	private boolean getCompressed = true;
	@XmlElement(name = "filterextensions")
	private FilterExtensions filterExtensions = new FilterExtensions();
	@XmlElement(name = "allowlogging")
	private boolean allowLogging = true;
	@XmlElement(name = "logtofile")
	private boolean logToFile = true;
	@XmlElement(name = "loglines")
	private int maxLogLines = 200;
	@XmlElement(name = "timezone")
	private String timezoneName = "";
	@XmlElement(name = "autoschedule")
	private boolean useAutoSchedule = true;
	@XmlElement(name = "sorttype")
	private int sortType = SORT_STATUS;
	@XmlElement(name = "sortdirection")
	private int sortDirection = SORT_ASCENDING;
	@XmlElement(name = "hdkeywords")
	private HDKeywords hdKeywords = new HDKeywords();
	@XmlElement(name = "hdpreference")
	private boolean hdDownloadPreference = false;
	@XmlElement(name = "useProxy")
	private boolean useProxy = false;
	@XmlElement(name = "proxyHost")
	private String proxyHost = "";
	@XmlElement(name = "proxyPort")
	private String proxyPort = "";
	@XmlElement(name = "useProxyAuth")
	private boolean useProxyAuth = false;
	@XmlElement(name = "proxyUsername")
	private String proxyUsername = "";
	@XmlElement(name = "proxyPassword")
	private String proxyPassword = "";
	@XmlElement(name = "filterPrivateTrackers")
	private boolean filterPrivateTrackers = true;
	@XmlElement(name = "ratioSeeders")
	private int ratioSeeders = 5;
	@XmlElement(name = "ratioLeechers")
	private int ratioLeechers = 1;
	
	private int timesParsedSinceLastCheck = 0;

	private final Color defaultEvenRowColor = Color.WHITE;
	private final Color defaultOddRowColor = new Color(236, 243, 254);

	private Color evenRowColor = Color.WHITE;
	private Color oddRowColor = new Color(236, 243, 254);
	private Color selectedRowColor = new Color(61, 128, 223);
	private Color gridColor = new Color(205, 205, 205);

	private List<String> privateTrackers = new ArrayList<String>();

	private static TedConfig configSingleton = null;

	/****************************************************
	 * CONSTRUCTORS
	 ****************************************************/
	/**
	 * Creates a TedConfig with some default values
	 */
	private TedConfig() {
	}

	// Handle multi threading problems. Only allow one singleton to be made.
	private synchronized static void createInstance() {
		if (configSingleton == null) {
			configSingleton = new TedConfig();
		}
	}

	public static TedConfig getInstance() {
		if (configSingleton == null) {
			createInstance();
		}

		return configSingleton;
	}

	// Prevent cloning.
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public void create(InputStream is) throws JAXBException{
        JAXBContext context = JAXBContext.newInstance(TedConfig.class);
        Unmarshaller um = context.createUnmarshaller();
		configSingleton = (TedConfig) um.unmarshal(is);
	}
	
	/****************************************************
	 * GETTERS & SETTERS
	 ****************************************************/

	/**
	 * @return Returns the directory where ted has to save the torrents
	 */
	public String getDirectory() {
		if (Directory.equals("")) {
			// init object
			Directory = new JFileChooser().getFileSystemView()
					.getDefaultDirectory().getAbsolutePath();
			String seperator = System.getProperty("file.separator");
			Directory += seperator + "ted";
		}
		return Directory;
	}

	/**
	 * Sets the directory where ted has to save the torrents he downloads
	 *
	 * @param directory
	 */
	@XmlTransient
	public void setDirectory(String directory) {
		Directory = directory;
	}

	/**
	 * @return Returns the time (in seconds) between the parser intervals
	 */
	public int getRefreshTime() {
		// convert to minutes
		return RefreshTime;
	}

	/**
	 * Set the time (in seconds) between two parser rounds
	 *
	 * @param refreshTime
	 */
	@XmlTransient
	public void setRefreshTime(int refreshTime) {
		// in minutes
		RefreshTime = refreshTime;
	}

	/**
	 * @return Returns if the user wants to see the errors
	 */
	public boolean isShowErrors() {
		return ShowErrors;
	}

	/**
	 * Set if the user wants to see messages when errors occur
	 *
	 * @param showErrors
	 */
	@XmlTransient
	public void setShowErrors(boolean showErrors) {
		ShowErrors = showErrors;
	}

	/**
	 * @return Returns if the user wants to see hurray messages
	 */
	public boolean isShowHurray() {
		return ShowHurray;
	}

	/**
	 * Set if the user wants to see hurray messages
	 *
	 * @param showHurray
	 */
	@XmlTransient
	public void setShowHurray(boolean showHurray) {
		ShowHurray = showHurray;
	}

	/**
	 * @return Returns if the user wants ted to open downloaded torrents in a
	 *         default torrent client
	 */
	public boolean isOpenTorrent() {
		return OpenTorrent;
	}

	/**
	 * Set if the user wants ted to open a downloaded torrent
	 *
	 * @param openTorrent
	 */
	@XmlTransient
	public void setOpenTorrent(boolean openTorrent) {
		OpenTorrent = openTorrent;
	}

	/**
	 *
	 * @return Returns if the user wants to download torrents to show specific
	 *         folders
	 */
	public boolean isDownloadToShowFolders() {
		return downloadToShowFolders;
	}

	/**
	 * Set if the user wants to download torrents to show specific folders
	 *
	 * @param downloadToShowFolders
	 */
	@XmlTransient
	public void setDownloadToShowFolders(boolean downloadToShowFolders) {
		this.downloadToShowFolders = downloadToShowFolders;
	}

	/**
	 *
	 * @return Returns if the user wants to download torrents to season specific
	 *         folders
	 */
	public boolean isDownloadToSeasonFolders() {
		return downloadToSeasonFolders;
	}

	/**
	 * Set if the user wants to download torrents to season specific folders
	 *
	 * @param downloadToSeasonFolders
	 */
	@XmlTransient
	public void setDownloadToSeasonFolders(boolean downloadToSeasonFolders) {
		this.downloadToSeasonFolders = downloadToSeasonFolders;
	}

	/**
	 * @return Returns if the user wants ted to check his version at startup
	 */
	public boolean isCheckVersion() {
		return CheckVersion;
	}

	/**
	 * Set if the user wants ted to check his version at startup
	 *
	 * @param checkVersion
	 */
	@XmlTransient
	public void setCheckVersion(boolean checkVersion) {
		CheckVersion = checkVersion;
	}

	/**
	 * @return Returns the user wants ted to start minimized
	 */
	public boolean isStartMinimized() {
		return StartMinimized;
	}

	/**
	 * Set if the user wants ted to start minimized
	 *
	 * @param startMinimized
	 */
	@XmlTransient
	public void setStartMinimized(boolean startMinimized) {
		StartMinimized = startMinimized;
	}

	/**
	 * @return Returns the stored height of the mainwindow.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param h
	 *            The height of the mainwindow to set.
	 */
	@XmlTransient
	public void setHeight(int h) {
		height = h;
	}

	/**
	 * @return Returns the width of the mainwindow.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param w
	 *            The width of the mainwindow to set.
	 */
	@XmlTransient
	public void setWidth(int w) {
		width = w;
	}

	/**
	 * @return Returns the x of the mainwindow.
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x_pos
	 *            The x of the mainwindow to set.
	 */
	@XmlTransient
	public void setX(int x_pos) {
		x = x_pos;
	}

	/**
	 * @return Returns the y of the mainwindow.
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y_pos
	 *            The y of the mainwindow to set.
	 */
	@XmlTransient
	public void setY(int y_pos) {
		y = y_pos;
	}

	/**
	 * @return Returns if the user wants to download a new season when ted
	 *         encounters one
	 */
	public boolean isDownloadNewSeason() {
		return downloadNewSeason;
	}

	/**
	 * Set if the user wants to download a new season when ted encounters one
	 *
	 * @param download
	 */
	@XmlTransient
	public void setDownloadNewSeason(boolean download) {
		downloadNewSeason = download;
	}

	/**
	 * @return Returns the number of the latest downloaded RSS feeds.
	 */
	public int getRSSVersion() {
		return RSSVersion;
	}

	/**
	 * @param version
	 *            The RSSVersion of the latest downloaded RSS feeds.
	 */
	@XmlTransient
	public void setRSSVersion(int version) {
		RSSVersion = version;
	}

	/**
	 * @return If the feeds should be auto-adjusted
	 */
	public boolean isAutoAdjustFeeds() {
		return (autoAdjustFeeds == ALWAYS);
	}

	/**
	 * Set the auto-adjustment of feeds
	 *
	 * @param adjust
	 */
	@XmlTransient
	public void setAutoAdjustFeeds(int adjust) {
		autoAdjustFeeds = adjust;
	}

	/**
	 * @return If the user wants to be asked before autoadjustement of the feeds
	 */
	public boolean askAutoAdjustFeeds() {
		return (autoAdjustFeeds == ASK);
	}

	/**
	 * @return If the feed list should be auto-updated
	 */
	public boolean isAutoUpdateFeedList() {
		return (autoUpdateFeedList == ALWAYS);
	}

	/**
	 * @return If the user wants to be asked before the feedslist is updated
	 */
	public boolean askAutoUpdateFeedList() {
		return (autoUpdateFeedList == ASK);
	}

	/**
	 * @return Auto-update of the feedlist
	 */
	public int getAutoUpdateFeedList() {
		return autoUpdateFeedList;
	}

	/**
	 * Set the auto-update of the feedlist
	 *
	 * @param update
	 */
	@XmlTransient
	public void setAutoUpdateFeedList(int update) {
		autoUpdateFeedList = update;
	}

	/**
	 * @return If the feeds should be auto-adjusted
	 */
	public int getAutoAdjustFeeds() {
		return autoAdjustFeeds;
	}

	/**
	 * @return Returns the timeOutInSecs.
	 */
	public int getTimeOutInSecs() {
		return TimeOutInSecs;
	}

	/**
	 * @param timeOutInSecs
	 *            The timeOutInSecs to set.
	 */
	@XmlTransient
	public void setTimeOutInSecs(int timeOutInSecs) {
		TimeOutInSecs = timeOutInSecs;
	}

	/**
	 * @return Returns the seederSetting.
	 */
	public int getSeederSetting() {
		return SeederSetting;
	}

	/**
	 * @param seederSetting
	 *            The seederSetting to set.
	 */
	@XmlTransient
	public void setSeederSetting(int seederSetting) {
		SeederSetting = seederSetting;
	}

	/**
	 * @return Returns the current locale.
	 */
	@XmlTransient
	public Locale getLocale() {
		Locale l = Locale.getDefault();
		if (locale_language.length() > 0 && locale_country.length() > 0) {
			l = new Locale(locale_language, locale_country);
		}
		return l;
	}

	/**
	 * @param language
	 *            The language to set.
	 */
	public void setLocale(Locale locale) {
		locale_language = locale.getLanguage();
		locale_country = locale.getCountry();
	}

	/**
	 * @return Returns current language code (eg en for english)
	 */
	public String getLanguage() {
		return locale_language;
	}

	/**
	 * @return current country code (eg US for United States)
	 */
	public String getCountry() {
		return locale_country;
	}

	/**
	 * @param country
	 *            The country
	 * @param language
	 *            The language
	 */

	public void setLocale(String country, String language) {
		locale_language = language;
		locale_country = country;
	}

	/**
	 * @return Should ted parse at startup?
	 */
	public boolean isParseAtStart() {
		return parseAtStart;
	}

	/**
	 * Set if ted should parse at startup
	 * @param b
	 */
	@XmlTransient
	public void setParseAtStart(boolean b) {
		parseAtStart = b;
	}

	/**
	 * @return Should ted add a systray?
	 */
	public boolean isAddSysTray() {
		return addSysTray;
	}

	/**
	 * Set the add systray setting
	 *
	 * @param b
	 */
	@XmlTransient
	public void setAddSysTray(boolean b) {
		addSysTray = b;
	}

	/**
	 * @return The download torrents with compressed files setting
	 */
	public boolean getDoNotDownloadCompressed() {
		return getCompressed;
	}

	/**
	 * Set the download torrents with compressed files settings
	 *
	 * @param b
	 */
	@XmlTransient
	public void setDoNotDownloadCompressed(boolean b) {
		getCompressed = b;
	}

	/**
	 * Get the extensions set by the user to filter torrents with compressed
	 * files
	 *
	 * @return
	 */
	public String getFilterExtensions() {
		return filterExtensions.getFilterExtensions();
	}

	/**
	 * Set the extensions used in the filtering of torrents with compressed
	 * files
	 *
	 * @param text
	 */
	@XmlTransient
	public void setFilterExtensions(String text) {
		filterExtensions.setFilterExtensions(text);
	}

	/**
	 *
	 * @return Return the file extensions to filter
	 */
	public List<String> getFilterExtensionsList() {
		return filterExtensions.getExtensions();
	}

	/**
	 * Set the file extensions to filter
	 * @param values
	 */
	public void setFilterExtensions(List<String> values) {
		filterExtensions.setExtensions(values);
	}

	/**
	 * Get the number of times that ted has searched for new episodes after the
	 * last update check
	 *
	 * @return
	 */
	public int getTimesParsedSinceLastCheck() {
		return timesParsedSinceLastCheck;
	}

	/**
	 * Set the number of times that ted searched for new shows after the last
	 * update check
	 *
	 * @param timesParsed
	 */
	@XmlTransient
	public void setTimesParsedSinceLastCheck(int timesParsed) {
		timesParsedSinceLastCheck = timesParsed;
	}

	/**
	 * @return If ted should keep a log
	 */
	public boolean isAllowLogging() {
		return allowLogging;
	}

	/**
	 * Set the log setting
	 *
	 * @param allowLog
	 */
	@XmlTransient
	public void setAllowLogging(boolean allowLog) {
		allowLogging = allowLog;
	}

	/**
	 * @return If ted should write the log to a file
	 */
	public boolean isLogToFile() {
		return logToFile;
	}

	/**
	 * Set the log to file setting
	 *
	 * @param logToFile2
	 */
	@XmlTransient
	public void setLogToFile(boolean logToFile2) {
		logToFile = logToFile2;
		TedLog.getInstance().setWriteToFile(logToFile);
	}

	/**
	 *
	 * @return Return the evenr row color as an RGB value
	 */
	@XmlElement(name = "evenrowcolor")
	public int getEvenRowColorRGB() {
		return evenRowColor.getRGB();
	}

	/**
	 * Set the even row color with an RGB value
	 * @param evenRowColor
	 */
	public void setEvenRowColorRGB(int evenRowColor) {
		this.evenRowColor = new Color(evenRowColor);
	}

	/**
	 *
	 * @return Return the even row color as an AWT color class
	 */
	public Color getEvenRowColor() {
		return evenRowColor;
	}

	/**
	 * Set the Even Row color with an AWT color class
	 * @param evenRowColor
	 */
	@XmlTransient
	public void setEvenRowColor(Color evenRowColor) {
		this.evenRowColor = evenRowColor;
	}

	/**
	 *
	 * @return Return the Odd Row color as an RGB value
	 */
	@XmlElement(name = "oddrowcolor")
	public int getOddRowColorRGB() {
		return this.oddRowColor.getRGB();
	}

	/**
	 * Set the Odd Row Color with an RGB value
	 * @param setOddRowColor
	 */
	public void setOddRowColorRGB(int setOddRowColor) {
		this.oddRowColor = new Color(setOddRowColor);
	}

	/**
	 *
	 * @return Return the Odd Row color as an AWT color class
	 */
	public Color getOddRowColor() {
		return oddRowColor;
	}

	/**
	 * Set the Odd Row color as an AWT color class
	 * @param oddRowColor
	 */
	@XmlTransient
	public void setOddRowColor(Color oddRowColor) {
		this.oddRowColor = oddRowColor;
	}

	/**
	 *
	 * @return Return the Selected Row color as an AWT Color class
	 */
	public Color getSelectedRowColor() {
		return selectedRowColor;
	}

	/**
	 * Set the Selected Row color with an AWT color Class
	 * @param selectedRowColor
	 */
	@XmlTransient
	public void setSelectedRowColor(Color selectedRowColor) {
		this.selectedRowColor = selectedRowColor;
	}

	/**
	 *  Set the Selected Row color with an RGB value
	 * @param selectedRowColor
	 */
	public void setSelectedRowColorRGB(int selectedRowColor) {
		this.selectedRowColor = new Color(selectedRowColor);
	}

	/**
	 *
	 * @return Return the Grid Color as an AWT color class
	 */
	public Color getGridColor() {
		return gridColor;
	}

	/**
	 * set the row colors back to default
	 */
	public void restoreDefaultColors() {
		setEvenRowColor(defaultEvenRowColor);
		setOddRowColor(defaultOddRowColor);
	}

	/**
	 *
	 * @return Return the current Timezone offset from UTC
	 */
	public int getTimeZoneOffset() {
		TimeZone tz = TimeZone.getTimeZone(this.timezoneName);
		return tz.getOffset(new Date().getTime());
		// return this.timeZoneOffset;
	}

	/**
	 * @return the timezoneName
	 */
	public String getTimezoneName() {
		return timezoneName;
	}

	/**
	 * @param timezoneName
	 *            the timezoneName to set
	 */
	@XmlTransient
	public void setTimezoneName(String timezoneName) {
		this.timezoneName = timezoneName;
	}

	/**
	 *
	 * @return Return if user wants to use auto schedule
	 */
	public boolean isUseAutoSchedule() {
		return useAutoSchedule;
	}

	/**
	 * Set if user wants to use auto schedule
	 * @param useAutoSchedule
	 */
	@XmlTransient
	public void setUseAutoSchedule(boolean useAutoSchedule) {
		this.useAutoSchedule = useAutoSchedule;
	}

	/**
	 * @return The type of field the maintable should be sorted on. 0 = no sort,
	 *         1 = on name, 2 = on status and airdate
	 */
	public int getSortType() {
		return sortType;
	}

	/**
	 * @param sortType
	 *            Type of sort that should be applied to the maintable
	 */
	@XmlTransient
	public void setSortType(int sortType) {
		this.sortType = sortType;
	}

	/**
	 * @return The direction of sorting for the maintable 0 = ascensing, 1 =
	 *         descending
	 */
	public int getSortDirection() {
		return sortDirection;
	}

	/**
	 * @param direction
	 *            Direction of sort for the maintable
	 */
	@XmlTransient
	public void setSortDirection(int direction) {
		this.sortDirection = direction;
	}

	@XmlTransient
	public void setHDKeywords(String keywords) {
		hdKeywords.setKeywords(keywords);
	}

	public String getHDKeywords() {
		return hdKeywords.getKeywordsString();
	}

	@XmlTransient
	public void setHDKeywordsList(List<String> keywords) {
		hdKeywords.setKeywords(keywords);
	}

	public List<String> getHDKeywordsList() {
		return hdKeywords.getKeywords();
	}

	public boolean isHDDownloadPreference() {
		return hdDownloadPreference;
	}

	@XmlTransient
	public void setHDDownloadPreference(boolean hdDownloadPreference) {
		this.hdDownloadPreference = hdDownloadPreference;
	}

	public boolean getUseProxy() {
		return this.useProxy;
	}

	@XmlTransient
	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	@XmlTransient
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	@XmlTransient
	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	public boolean getUseProxyAuth() {
		return useProxyAuth;
	}

	@XmlTransient
	public void setUseProxyAuth(boolean useProxyAuth) {
		this.useProxyAuth = useProxyAuth;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	@XmlTransient
	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	@XmlTransient
	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public boolean isPrivateTracker(String trackerName) {
		return privateTrackers.contains(trackerName);
	}

	@XmlTransient
	public void setPrivateTrackers(List<String> privateTrackers) {
		this.privateTrackers = privateTrackers;
	}

	public boolean isFilterPrivateTrackers() {
		return filterPrivateTrackers;
	}

	@XmlTransient
	public void setFilterPrivateTrackers(boolean filterThem) {
		filterPrivateTrackers = filterThem;
	}

	/**
	 * @return the maxLogLines
	 */
	public int getMaxLogLines() {
		return maxLogLines;
	}

	/**
	 * @param maxLogLines
	 *            the maxLogLines to set
	 */
	@XmlTransient
	public void setMaxLogLines(int maxLogLines) {
		this.maxLogLines = maxLogLines;
	}
	

	@XmlTransient
	public void setRatioSeeders(int ratio)
	{
		ratioSeeders = ratio;
	}
	
	@XmlTransient
	public void setRatioLeechers(int ratio)
	{
		ratioLeechers = ratio;
	}
	
	public int getRatioSeeders()
	{
		return ratioSeeders;
	}
	
	public int getRatioLeechers()
	{
		return ratioLeechers;
	}

	/****************************************************
	 * PRIVATE CLASSES
	 ****************************************************/
	private static class FilterExtensions {

		private final String seperator = ",";

		@XmlElement(name = "extension")
		private final List<String> extensions = new ArrayList<String>();
		{
			extensions.add("zip");
			extensions.add("rar");
			extensions.add("r01");
		}

		public List<String> getExtensions() {
			return extensions;
		}

		@XmlTransient
		public void setExtensions(List<String> newExtensions) {
			extensions.clear();
			extensions.addAll(newExtensions);
		}

		@XmlTransient
		public void setFilterExtensions(String text) {
			if (!text.equals("")) {
				extensions.clear();
				String[] result = text.split(seperator);
				for (int x = 0; x < result.length; x++)
					extensions.add(result[x].trim());
			}
		}

		public String getFilterExtensions() {
			String filterExtensions = "";
			for (int i = 0; i < extensions.size(); i++) {
				if (i != 0)
					filterExtensions += seperator + " ";

				filterExtensions += extensions.get(i);
			}
			return filterExtensions;
		}
	}

	private static class HDKeywords {

		private final String seperator = "&";

		@XmlElement(name = "keyword")
		private final List<String> keywords = new ArrayList<String>();
		{
			keywords.add("720p");
			keywords.add("HD");
		}

		public List<String> getKeywords() {
			return keywords;
		}

		@XmlTransient
		public void setKeywords(List<String> newKeywords) {
			keywords.clear();
			keywords.addAll(newKeywords);
		}

		@XmlTransient
		public void setKeywords(String text) {
			if (!text.equals("")) {
				keywords.clear();
				String[] result = text.split(seperator);
				for (int x = 0; x < result.length; x++)
					keywords.add(result[x].trim());
			}
		}

		public String getKeywordsString() {
			String filterExtensions = "";
			for (int i = 0; i < keywords.size(); i++) {
				if (i != 0)
					filterExtensions += " " + seperator + " ";

				filterExtensions += keywords.get(i);
			}
			return filterExtensions;
		}
	}
}
