package ted;

/**
 * TED: Torrent Episode Downloader (2005 - 2006)
 * 
 * This is the counter that tokenizes a string on certain keyword tags
 * like !, | and &
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
public class TedKeywordTokenizer 
{
	/****************************************************
	 * GLOBAL VARIABLES
	 ****************************************************/
	String string;
	int index;
	
	/****************************************************
	 * CONSTRUCTORS
	 ****************************************************/
	/**
	 * Creates a TedKeywordTokenizer
	 * @param s String to tokenize
	 */
	public TedKeywordTokenizer(String s)
	{
		this.string = s;
		index = 0;
	}
	
	/****************************************************
	 * LOCAL METHODS
	 ****************************************************/	
	private boolean isToken (String s)
	{
		// check if the string is one of the tokens
		if (s.equals("(") || s.equals(")") || s.equals("&") || s.equals("|") || s.equals("!") || s.equals(","))
		{
			return true;
		}
		return false;
	}
	
	/****************************************************
	 * PUBLIC METHODS
	 ****************************************************/
	/**
	 * @return If there are more tokens left
	 */
	public boolean hasMoreTokens() 
	{
		if (index < string.length())
		{
			return true;
		}
		return false;
	}

	/**
	 * @return The next token
	 */
	public String nextToken() 
	{
		String result = "";
		// walk from index until we find a string that matches our keyword operators
		// (, ), |, &, ! or ,
		int i;
		for (i = index; i < string.length(); i++)
		{
			String substring = string.substring(i, i+1);
			
			// if it is a token on the first place, return it
			if (isToken(substring) && i == index)
			{
				index = i+1;
				return substring;
			}
			// if it is a token on another place, return what we found until then
			else if (isToken(substring) && i != index)
			{
				index = i;
				return result;
			}
			else
			{
				result = result + substring;
			}
		}
		index = i;
		return result;
	}

	/**
	 * @return Everything that is left in the tokenizer
	 */
	public String getRestOfString() 
	{
		// return everything from index till end of string
		return string.substring(index, string.length());
	}

}
