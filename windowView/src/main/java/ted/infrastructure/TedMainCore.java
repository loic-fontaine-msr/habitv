package ted.infrastructure;

import java.util.Collection;
import java.util.Vector;

import ted.TedSerie;

public class TedMainCore implements ITedMain
{
    protected Vector<TedSerie> series = new Vector<TedSerie>();

    public void setSeries(Collection<TedSerie> c)
    {
    	this.series.clear();
    	this.series.addAll(c);
    }

    public Vector<TedSerie> getSeries()
    {
    	return this.series;
    }

    /**
     * Saves the shows that are in displayed in the ted window
     */
    public void saveShows()
    {
    	//TODO save config
    }

    public void saveConfig(boolean resetTime)
    {
    	//TODO save config
    }

    public void repaint()
    {
    	// update status file
    }


    public boolean getStopParsing()
    {
		// TODO Auto-generated method stub
		return false;
    }
}
