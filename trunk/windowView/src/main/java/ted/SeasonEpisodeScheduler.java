package ted;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import ted.datastructures.DailyDate;
import ted.datastructures.SeasonEpisode;
import ted.datastructures.StandardStructure;
import ted.datastructures.StandardStructure.AirDateUnknownException;

public class SeasonEpisodeScheduler implements Serializable
{	
	public class NoEpisodeFoundException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3156780544495682045L;

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2256145484789412126L;
	private TedSerie serie;
	// Vector containing the scheduled episodes. Sorted on airdate. First item is with highest airdate
	private Vector<StandardStructure> scheduledEpisodes;
	private Date checkEpisodeSchedule;
	private int updateIntervalInDays = 6;
	private boolean retrievalPublishedAndAiredInterrupted = false;
	
	public SeasonEpisodeScheduler (TedSerie serie)
	{
		this.serie = serie;
	}
	
	/**
	 * Copy constructor
	 * @param scheduler2
	 */
	public SeasonEpisodeScheduler (SeasonEpisodeScheduler scheduler2, TedSerie serie) 
	{
		if (this.scheduledEpisodes != null)
		{
			this.scheduledEpisodes.clear();
		}
		else
		{
			this.scheduledEpisodes = new Vector<StandardStructure>();
		}
		if (scheduler2.getScheduledEpisodes() != null)
		{
			this.scheduledEpisodes.addAll(scheduler2.getScheduledEpisodes());
		}
		this.checkEpisodeSchedule = scheduler2.checkEpisodeSchedule;
		this.updateIntervalInDays = scheduler2.getIntervalInDays();
		this.serie = serie;
	}
	
	/**
	 * @return A vector of episodes that are currently aired (from epguides info)
	 */
	private Vector<StandardStructure> getAiredEpisodes()
	{	
		Vector<StandardStructure> results = new Vector<StandardStructure>();
		
		if (this.isEpisodeScheduleAvailable())
		{
			// system date
	       	Date systemDate = new Date();
			
	       	StandardStructure current;
			// return only seasonepisodes aired until today
			for (int i = 0; i < this.scheduledEpisodes.size(); i++)
			{
				current = this.scheduledEpisodes.elementAt(i);
				try 
				{
					if (current.getAirDate().before(systemDate))
					{
						results.add(current);
					}
				} 
				catch (AirDateUnknownException e) 
				{
					continue;
				}
			}
		}
		
		return results;
	}
	
	/**
	 * @return The next episode scheduled to air. Will first search though all episodes
	 * from epguides. If nothing is found, it will generate one based on the last published episode.
	 * And if that also does not give a result (so no scheduled/published episodes) it will return a new episode
	 * (season 1 episode 1 or daily based on todays date)
	 */
	public StandardStructure getNextToAirEpisode()
	{
		StandardStructure result = null;
		
		// Keep track of the latest episode in the schedule which has 
		// an air date. With this episode the next episode in the list
		// can be found easily without using the getPublishedAndAired 
		// function.
		StandardStructure latestInScheduleWithDate = null;
		
		// If the schedule is known (if so, update).
		if (this.isEpisodeScheduleAvailable())
		{
			// nothing found while searching through the episodes with airdates
			// run through episodes
			// get first episode after all aired episodes, with presumable no airdate
			StandardStructure current;

			// system date
	       	Date systemDate = new Date();
	       	// first find last aired episode in list
			for (int i = 0 ; i < this.scheduledEpisodes.size(); i++)
			{
				current = this.scheduledEpisodes.elementAt(i);
								
				try 
				{
					// If there is no episode found with an air date set it.
					// Do this only for the latest episode in the list (with a date).
					if (   latestInScheduleWithDate == null 
						&& current.getAirDate()     != null)
					{
						latestInScheduleWithDate = current;
					}
					
					if (current.getAirDate().after(systemDate))
					{
						result = current;
					}
					else
					{
						break;
					}
				} 
				catch (AirDateUnknownException e) 
				{					
					continue;
				}
			}			
		}

		// Nothing found based on schedule, find one after the latest
		// one with an air date.
		if (result == null && latestInScheduleWithDate != null)
		{			
			try 
			{
				// Search for the next episode...
				result = getNextEpisode(latestInScheduleWithDate);
			} 
			catch (NoEpisodeFoundException e1) 
			{
				// ... if one isn't found just do a guess.
				result = latestInScheduleWithDate.guessNextEpisode();
			}
		}
		
		// nothing found, generate default 
		if (result == null)
		{
			if (serie.isDaily)
			{
				Date systemDate = new Date();
				result = new DailyDate(systemDate);
			}
			else
			{
				result = new SeasonEpisode(1, 1);
			}
		}
		
		return result;
	}
	
	
	/**
	 * @return Vector with season/episode combinations taken from the torrent
	 * feeds from this show
	 */
	private Vector<StandardStructure> getPublishedEpisodes()
	{
		Vector<StandardStructure> publishedSeasonEpisodes = new Vector<StandardStructure>(); //TODO
		return publishedSeasonEpisodes;
	}
	
	/**
	 * @return Whether the schedule is filled. Will trigger an episode schedule update when needed.
	 * (once every 7 days)
	 * Will also update the current set episode in the show when an update is done.
	 */
	public boolean isEpisodeScheduleAvailableWithUpdate(boolean forceUpdate)
	{
		// system date
       	Date systemDate = new Date();
       	       	
		// check date
		if ( serie.isUseAutoSchedule() && 
				( 	this.scheduledEpisodes == null || 
					this.checkEpisodeSchedule == null || 
					systemDate.after(this.checkEpisodeSchedule) ||
					forceUpdate)
				)
		{	
			// Get the show info: the air time and time zone.
			// Do this before retrieving the schedule as this information is used.
			// TvRage.parseTvRageShowInfo(serie);
			
			serie.setStatusString(Lang.getString("TedSerie.EpisodeScheduleUpdate"));
						
			// parse epguides
			// New instance of the parser
			//ScheduleParser tedEP = new ScheduleParser(); TODO guide parser
	        
	        this.scheduledEpisodes = new Vector<StandardStructure>();//.getScheduledSeasonEpisodes(serie);
	        
	        // one week from now
	        Calendar future = Calendar.getInstance();
	        future.add(Calendar.DAY_OF_YEAR, this.getIntervalInDays());
	        this.checkEpisodeSchedule = future.getTime();
	        	        
	        // update serie with scheduled episode
			try 
			{
				StandardStructure current = this.getEpisode(serie.getCurrentStandardStructure());
				serie.setCurrentEpisode(current);
			} 
			catch (NoEpisodeFoundException e) 
			{
				// do nothing
			}
				        
	        serie.resetStatus(true);
		}   
		
		return this.isEpisodeScheduleAvailable();
	}
	
	/**
	 * @return Is there schedule information available. This method won't trigger a schedule
	 * update.
	 */
	public boolean isEpisodeScheduleAvailable()
	{
		boolean result;
		if (this.scheduledEpisodes != null && this.scheduledEpisodes.size() > 0)
		{
			result = true;
		}
		else
		{
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Searches the episode in the future episode schedule
	 * @param season
	 * @param episode
	 * @return SeasonEpisode for parameters. null if episode is not planned in schedule
	 */
	public StandardStructure getEpisode(StandardStructure episodeToFind) throws NoEpisodeFoundException
	{
		StandardStructure result = null;
		
		// check schedule for updates
		if (this.isEpisodeScheduleAvailable())
		{
			// search for season, episode in vector
			for (int i = 0; i < this.scheduledEpisodes.size(); i++)
			{
				StandardStructure current = this.scheduledEpisodes.elementAt(i);
				if (current.compareTo(episodeToFind) == 0)
				{
					result = current;
					break;
				}	
			}
		}
		else
		{
			result = episodeToFind;
		}
		
		if (result == null)
		{
			NoEpisodeFoundException e = new NoEpisodeFoundException();
			throw e;
		}
		
		return result;
	}
	
	/**
	 * @param season
	 * @param episode
	 * @return Episode scheduled after season, episode parameters
	 * null if episode is not found
	 */
	public StandardStructure getNextEpisode (StandardStructure episodeToFind) throws NoEpisodeFoundException
	{
		StandardStructure result = null;
			
		// check schedule for updates
		if (this.isEpisodeScheduleAvailable())
		{
			StandardStructure tempResult = null;
			// search for season, episode in vector
			// find the next to air after episode to find.
			for (int i = 0; i < this.scheduledEpisodes.size(); i++)
			{
				StandardStructure current = this.scheduledEpisodes.elementAt(i);
				if (episodeToFind.compareTo(current) > 0)
				{
					tempResult = this.scheduledEpisodes.elementAt(i);
				}
				else if (episodeToFind.compareTo(current) <= 0)
				{
					// break early for performance reasons
					break;
				}
			}	
			result = tempResult;
		}

		if (result == null)
		{
			// throw exception and let client solve the problem when no future episodes are scheduled
			NoEpisodeFoundException e = new NoEpisodeFoundException();
			throw e;
		}
		return result;
	}

	/**
	 * Merges the published episodes (from the torrent websites) and the 
	 * aired episodes (from the epguides schedule).
	 * Adds availability of episodes on the torrent sites to the scheduled episodes
	 * retrieved from the tv schedules webistes
	 * @return a vector with season/episodes that have been aired plus
	 * one next episode that is scheduled to air
	 */
	Vector<StandardStructure> getPubishedAndAiredEpisodes() 
	{
		Vector<StandardStructure> publishedEpisodes = this.getPublishedEpisodes();
		Vector<StandardStructure> airedEpisodes     = this.getAiredEpisodes();
		Vector<StandardStructure> results           = new Vector<StandardStructure>();
		
		if (publishedEpisodes.size() > 0 && airedEpisodes.size() > 0 && !retrievalPublishedAndAiredInterrupted)
		{		
			results.addAll(airedEpisodes);
			// filter out any items in publishedEpisodes that are not in airedEpisodes
			int airedCounter = 0;
			int publishedCounter = 0;
			// get first elements
			StandardStructure publishedEpisode = publishedEpisodes.elementAt(publishedCounter);
			StandardStructure airedEpisode     = airedEpisodes.elementAt(airedCounter);		
			airedCounter++;
			publishedCounter++;
			
			while (airedCounter < results.size() && publishedCounter < publishedEpisodes.size()  && !retrievalPublishedAndAiredInterrupted)
			{
				// compare the two episodes.
				
				// if published > aired get next published
				if (publishedEpisode.compareTo(airedEpisode) < 0 && publishedCounter < publishedEpisodes.size())
				{
					publishedEpisode = publishedEpisodes.elementAt(publishedCounter);
					publishedCounter ++;					
				}
				// if published < aired get next aired
				else if (publishedEpisode.compareTo(airedEpisode) > 0 && airedCounter < airedEpisodes.size())
				{
					airedEpisode = results.elementAt(airedCounter);
					airedCounter ++;
				}
				// if published == aired, save episode into result vector and get next of both
				else if (publishedEpisode.compareTo(airedEpisode) == 0)
				{
					airedEpisode.setQuality(publishedEpisode.getQuality());
					
					if (publishedCounter < publishedEpisodes.size())
					{
						publishedEpisode = publishedEpisodes.elementAt(publishedCounter);
						publishedCounter ++;
					}
					if (airedCounter < airedEpisodes.size())
					{
						airedEpisode = results.elementAt(airedCounter);
						airedCounter ++;
					}
				}
			}
						
			// make sure to check the last episodes, could be skipped by while loop
			if (publishedEpisode.compareTo(airedEpisode) == 0)
			{
				airedEpisode.setQuality(publishedEpisode.getQuality());
			}		
		}
		else
		{
			// if there are no scheduled episodes, return the published episodes as result
			results = publishedEpisodes;
		}
		
		// free references for garbage collection
		airedEpisodes = null;
		publishedEpisodes = null;
		
		retrievalPublishedAndAiredInterrupted = false;
		return results;
	}
	
	/**
	 * Checks the airdate for the current season/episode of the show.
	 * Puts the show on hold if the airdate is in the future
	 * Puts the show on hiatus if a schedule is known but no airdate is found
	 */
	public void checkAirDate() 
	{		
		this.isEpisodeScheduleAvailable();
		// get airdate for current season / episode
		StandardStructure currentSE = serie.getCurrentStandardStructure();
		Date airDate;
		try 
		{
			airDate = currentSE.getAirDate();
			Date currentDate = new Date();
			
			if (currentDate.before(airDate))
			{
				// put serie on hold if airdate is after today
				serie.setStatus(TedSerie.STATUS_HOLD);
			}
			else
			{
				// put show on check
				serie.setStatus(TedSerie.STATUS_CHECK);
			}
		} 
		catch (AirDateUnknownException e) 
		{
			if (this.isEpisodeScheduleAvailable())
			{
				// if schedule is available, wait until airdate becomes known
				serie.setStatus(TedSerie.STATUS_HIATUS);
				serie.checkIfCurrentEpisodeIsScheduled();
			}
			else
			{
				// if schedule is not available, just put the show on check
				serie.setStatus(TedSerie.STATUS_CHECK);
			}		
		}

	}
	
	/**
	 * Check if the serie has to get another status depending on the day it is and
	 * the days the user selected to check the show again
	 */
	public void updateShowStatus() 
	{
		if (!serie.isDisabled())
		{		
			// if the show is on hiatus, check if there is already some
			// episode planning for the next episode available
			if (serie.isHiatus())
			{
				// only do this when current season / episode is not known in schedule
				// when the current episode is not known in the schedule,
				// get the next episode from the planning                       
				serie.checkIfCurrentEpisodeIsScheduled();
			}
						
			// check the airdate for the selected season/episode
			if (serie.isSerieAndGlobalUseAutoSchedule())
			{
				this.checkAirDate();
			}
			else
			{
				// If the auto scheduler isn't set put the show to check. 
				serie.setStatus(TedSerie.STATUS_CHECK);
			}
		}
	}
	
	public StandardStructure getLastAiredEpisode()
	{
		for (int i = 0; i < scheduledEpisodes.size(); ++i)
		{
			StandardStructure temp = scheduledEpisodes.elementAt(i);
			
			try 
			{
				Date date= temp.getAirDate(); 

				// system date
		       	Date systemDate = new Date();
		       	
				if (date != null && date.before(systemDate))
				{
					return temp;
				}
			} 
			catch (AirDateUnknownException e) 
			{
				// TODO: do something when date is unknown?
				continue;
			}
		}
		
		StandardStructure nothingFoundStructure = new StandardStructure();
		return nothingFoundStructure;
	}
	
	/**
	 * Forces an update of the schedule. Needed when the epguides id is changed.
	 */
	public void forceScheduleUpdate() 
	{
		// set update date to today
		this.checkEpisodeSchedule = new Date();
		this.isEpisodeScheduleAvailableWithUpdate(true);
	}

	/**
	 * Clears the current schedule and schedules an update
	 * @param tedSerie 
	 */
	public void clear(TedSerie tedSerie) 
	{
		if (this.scheduledEpisodes != null)
		{
			this.scheduledEpisodes.clear();
			this.scheduledEpisodes = null;
		}
		this.checkEpisodeSchedule = new Date();
		this.serie = tedSerie;
	}

	private Vector<StandardStructure> getScheduledEpisodes() 
	{
		return this.scheduledEpisodes;
	}

	public Date getLastUpdateDate() 
	{
		if (this.checkEpisodeSchedule != null)
		{
			// subtract the interval from the next update date
			Date temp = new Date();
			temp.setTime(this.checkEpisodeSchedule.getTime());
	        Calendar weekback = Calendar.getInstance();
	        weekback.setTime(temp);
	        weekback.add(Calendar.DAY_OF_YEAR, -(this.getIntervalInDays()));
	        return weekback.getTime();
		}
		else
		{
			return null;
		}
	}

	private int getIntervalInDays() 
	{		
		if (this.serie.isDaily)
		{
			// for a daily show, update the schedule more regularly
			this.updateIntervalInDays = 3;
		}
		else
		{
			this.updateIntervalInDays = 6;
		}
		
		return this.updateIntervalInDays;
	}

	public Date getNextUpdateDate() 
	{
		return this.checkEpisodeSchedule;
	}

	public void interruptPubishedAndAiredEpisodes() 
	{
		retrievalPublishedAndAiredInterrupted  = true;
		
		//TODO stop parser	
	}
}
