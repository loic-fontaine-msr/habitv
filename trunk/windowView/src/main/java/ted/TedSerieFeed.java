package ted;

import java.io.Serializable;
/**
 * TED: Torrent Episode Downloader (2005 - 2006)
 * 
 * This class will hold data for one feed
 * 
 * @author Roel
 * @author Joost
 *
 * ted License:
 * This file is part of ted. ted and all of it's parts are licensed
 * under GNU General Public License (GPL) version 2.0
 * 
 * for more details see: http://en.wikipedia.org/wiki/GNU_General_Public_License
 *
 */

public class TedSerieFeed implements Serializable
{
	private static final long serialVersionUID = 8455093524452410549L;
	/****************************************************
	 * GLOBAL VARIABLES
	 ****************************************************/
	private String url;
	private boolean selfMade = false;
	
	// NOTE::
	// DO NOTE REMOVE THESE FIELDS HERE!!
	// that will break the backwards compatibility of this class
	
	// DEPRECATED FIELDS: only here for backwards compatibility.
	private long checkdate;
	
	/****************************************************
	 * CONSTRUCTOR
	 ****************************************************/
	public TedSerieFeed (String u, long date)
	{
		this.setUrl(u);
		this.setSelfMade(false);
	}
	
	public TedSerieFeed (String u, boolean b)
	{
		this.setUrl(u);
		this.setSelfMade(b);
	}
	
	/****************************************************
	 * GETTERS & SETTERS
	 ****************************************************/

	/**
	 * @return Returns the url.
	 */
	public String getUrl()
	{
		this.url = this.url.replace(" ", "%20");
		return url;
	}

	public String getFilledURL(String name, int season, int episode)
	{
		String url = this.getUrl();
		url = url.replaceAll("#NAME#", name.replaceAll(" ", "%20"));
		url = url.replaceAll("#SEASON#", Integer.toString(season));
		url = url.replaceAll("#EPISODE#", Integer.toString(episode));
		return url;
	}

	/**
	 * @param url The url to set.
	 */
	public void setUrl(String url)
	{
		this.url = url.replace(" ", "%20");
	}
	
	public boolean getSelfmade()
	{
		return this.selfMade;
	}
	
	private void setSelfMade(boolean b)
	{
		this.selfMade = b;
	}
}
