package ted.datastructures;

import java.io.Serializable;

/**
 * @author Roel
 *
 */
public class SimpleTedSerie implements Comparable<SimpleTedSerie>, Serializable
{
	private String name;

	public SimpleTedSerie()
	{}

	public SimpleTedSerie(String name)
	{
		this.name = name;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	public String getDisplayName()
	{
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	public int compareTo(SimpleTedSerie second)
	{
		String firstName  = removeTheFromName(this.getName().toLowerCase());
		String secondName = removeTheFromName(second.getName().toLowerCase());

		return firstName.compareTo(secondName);
	}

	private String removeTheFromName(String name)
	{
		if (name.startsWith("the "))
		{
			name = name.substring(4);
		}

		return name;
	}

	/**
	 * @return A vector of episodes that are currently aired (from epguides info)
	 * and the next episode
	 */
//	public Vector<StandardStructure> getAiredEpisodes()
//	{
//		// New instance of the parser
//        EpguidesParser tedEP = new EpguidesParser();
//
//        return tedEP.getPastSeasonEpisodes(this.getName());
//	}


}
