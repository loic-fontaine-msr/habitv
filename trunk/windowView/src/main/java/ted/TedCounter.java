package ted;


/**
 * TED: Torrent Episode Downloader (2005 - 2006)
 *
 * This is the counter that counts down to the next parse round of ted
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
public class TedCounter extends Thread
{
	/****************************************************
	 * GLOBAL VARIABLES
	 ****************************************************/
	private int count;
	private TedMainDialog main;

	/****************************************************
	 * CONSTRUCTOR
	 ****************************************************/
	/**
	 * Create a new counter
	 * @param m the TedMainDialog
	 * @param tc the current config
	 */
	public TedCounter(TedMainDialog m)
	{
		main = m;

		if(TedConfig.getInstance().isParseAtStart())
			count = 0;
		else
			count = TedConfig.getInstance().getRefreshTime();
	}

	/****************************************************
	 * PUBLIC METHODS
	 ****************************************************/

	public void run()
	{
		while (count > 0)
		{
			if (count % 60 == 0)
			{
				// check if a minute passed
				main.updateCounter((int)Math.floor(count/60));
			}
			try
			{
				// sleep for a min
				count -= 60;
				sleep(60000);
			}
			catch (Exception e)
			{
				TedLog.getInstance().error(e, "counter error");
			}
		}

		if (count == 0)
		{
			main.updateCounter(0);
			main.parseShows();
			count = TedConfig.getInstance().getRefreshTime();
			this.run();
		}
	}

	/**
	 * Update the conter to the refreshtime that is currently set in the config
	 */
	public void updateRefreshTime()
	{
		this.count = TedConfig.getInstance().getRefreshTime();
		main.updateCounter((int)Math.floor(count/60));
	}

	/****************************************************
	 * GETTERS & SETTERS
	 ****************************************************/

	/**
	 * Set the counter
	 * @param i
	 */
	public void setCount(int i)
	{
		count = i;
	}

	/**
	 * @return Current count
	 */
	public int getCount()
	{
		return count;
	}
}
