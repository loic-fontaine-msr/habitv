package ted.ui.addshowdialog;

import java.util.Vector;

import ted.TedSerie;
import ted.datastructures.StandardStructure;

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
public class EpisodeParserThread extends Thread 
{
	/****************************************************
	 * GLOBAL VARIABLES
	 ****************************************************/
	private EpisodeChooserPanel episodeChooserPanel;
	private TedSerie selectedSerie;
	private SubscribeOptionsPanel subscribeOptionsPanel = null;
	private boolean done = false;
	
	/****************************************************
	 * CONSTRUCTOR
	 ****************************************************/
	/**
	 * Create a new counter
	 * @param subscribeOptionsPanel 
	 * @param m the TedMainDialog
	 * @param tc the current config
	 */
	public EpisodeParserThread(EpisodeChooserPanel ecp, TedSerie ts, SubscribeOptionsPanel subscribeOptionsPanel)
	{
		this.episodeChooserPanel = ecp;
		this.selectedSerie = ts;
		this.subscribeOptionsPanel = subscribeOptionsPanel;
		done = false;
	}
	
	public EpisodeParserThread(EpisodeChooserPanel episodeChooserPanel2,
			TedSerie show)
	{
		this.episodeChooserPanel = episodeChooserPanel2;
		this.selectedSerie = show;
		done = false;
	}

	/****************************************************
	 * PUBLIC METHODS
	 ****************************************************/
	
	public void run()
	{
		this.episodeChooserPanel.clear();
		if (this.subscribeOptionsPanel != null)
		{
			this.subscribeOptionsPanel.clear();
			this.subscribeOptionsPanel.setGlobalActivityStatus(true);
		}
		else
		{
			this.episodeChooserPanel.setActivityStatus(true);
		}
		
		if (selectedSerie!=null && !done)
		{	
			StandardStructure nextEpisode = null;
			boolean scheduleAvailable = true;
			boolean isSubscribeOptionsPanel = (this.subscribeOptionsPanel != null);
			// check if schedule is available
			if (selectedSerie.isEpisodeScheduleAvailableWithUpdate() && !done)
			{
				// get next to air episode
				nextEpisode = selectedSerie.getNextEpisode();
				
				if (isSubscribeOptionsPanel && !done)
				{		
					this.subscribeOptionsPanel.setGlobalActivityStatus(false);
					this.subscribeOptionsPanel.setCustomActivityStatus(true);
					this.subscribeOptionsPanel.setNextEpisode(nextEpisode);
					this.subscribeOptionsPanel.setLastAiredEpisode(selectedSerie.getLastAiredEpisode());	
				}
			}
			else
			{
				// no schedule, extra work is needed to 'guess' the last aired and next to air episode
				// based on the episodes retrieved from the torrent websites
				scheduleAvailable = false;
			}
			
			// Retrieve published torrents from the torrent websites.
			// Do this at the end. This way we save some time loading the
			// episodes in the custom episode list.
			Vector<StandardStructure> publishedEpisodes = selectedSerie.getPubishedAndAiredEpisodes();	
			
			// if no schedule, also fill the subscribeoptionspanel with info from the torrent sites
			if (!scheduleAvailable && publishedEpisodes.size() > 0 && isSubscribeOptionsPanel && !done)
			{
				StandardStructure lastAiredGuess = publishedEpisodes.elementAt(0);
				this.subscribeOptionsPanel.setLastAiredEpisode(lastAiredGuess);
				nextEpisode = lastAiredGuess.guessNextEpisode();	
				this.subscribeOptionsPanel.setNextEpisode(nextEpisode);
				this.subscribeOptionsPanel.setGlobalActivityStatus(false);
				this.subscribeOptionsPanel.setCustomActivityStatus(true);
			}
			
			// add episodes to episodechooserpanel
			if (!done)
			{
				this.episodeChooserPanel.setSeasonEpisodes(publishedEpisodes);
			}
			
			if (nextEpisode != null && !done)
			{
				this.episodeChooserPanel.setNextEpisode(nextEpisode);
			}
			
			if (isSubscribeOptionsPanel && !done)
			{
				this.subscribeOptionsPanel.enableCustomEpisodes();
				this.subscribeOptionsPanel.setCustomActivityStatus(false);
			}
			else if (!done)
			{
				this.episodeChooserPanel.setActivityStatus(false);
			}
			
			// free vector for garbage collection
			publishedEpisodes = null;
		}
		
		// disable activity image	
		this.episodeChooserPanel.selectEpisode();
	}

	/**
	 * Stop parsing the episodes and end the thread
	 */
	public void done() 
	{
		done = true;	
		selectedSerie.interruptPubishedAndAiredEpisodes();
	}
}
