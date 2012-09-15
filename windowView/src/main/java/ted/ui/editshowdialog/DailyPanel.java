package ted.ui.editshowdialog;
import java.util.GregorianCalendar;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;

import ted.Lang;
import ted.TedDailySerie;
import ted.datastructures.DailyDate;
import ted.datastructures.StandardStructure;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
/**
 * Class to display the settings for a daily show in the edit show dialog
 * @author roel
 *
 */
public class DailyPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7912966362467037259L;
	DatePanel datePanel;
	private JLabel maxEpisodesLabel2;
	private JSpinner episodeSpinner;
	private SpinnerListModel episodeSpinnerModel;
	private DailyDate currentDate;

	public DailyPanel ()
	{
		this.initGUI();
	}
	
	private void initGUI() {
		try {
			{
				
				FormLayout thisLayout = new FormLayout(
					"max(p;5dlu), max(p;5dlu), 5dlu, 15dlu:grow, max(p;5dlu)",
					"max(p;5dlu), max(p;5dlu), max(p;5dlu)");
				this.setLayout(thisLayout);
				this.setPreferredSize(new java.awt.Dimension(290, 67));
			}
			{
				datePanel = new DatePanel();
				this.add(datePanel, new CellConstraints("2, 3, 4, 1, left, default"));
				
				episodeSpinner = new JSpinner();
				String[] items = new String[21];
				items[0]=Lang.getString("TedGeneral.All");
				for(int i=1; i<21; i++)
					items[i]=""+i;
				episodeSpinnerModel = new SpinnerListModel(items);
				
				this.add(episodeSpinner, new CellConstraints("2, 2, 1, 1, default, default"));
				
				episodeSpinner.setModel(episodeSpinnerModel);
				episodeSpinner.setPreferredSize(new java.awt.Dimension(62, 21));
				//Integer value = new Integer(1);
				//this.episodeSpinnerModel.setMinimum(value);
			}
			{
				maxEpisodesLabel2 = new JLabel();
				this.add(maxEpisodesLabel2, new CellConstraints("4, 2, 1, 1, default, default"));
				maxEpisodesLabel2.setText(Lang.getString("TedEpisodeDialog.LabelDailyMaxEpisodes2"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the values of the daily show in the panel
	 * @param serie
	 */
	public void setValues(TedDailySerie serie) 
	{
		// set latest download date
		//this.datePanel.setDate(serie.getLatestDownloadDateInMillis());
		this.currentDate = (DailyDate)(serie.getCurrentStandardStructure());
		this.datePanel.setDate(currentDate.getDate().getTimeInMillis());
		
		// get number of episodes to download
		Integer episodes = new Integer (serie.getMaxDownloads());
		if(episodes.intValue()>0)
			this.episodeSpinner.setValue(""+episodes.intValue());
		else
		{
			this.episodeSpinner.setValue(Lang.getString("TedGeneral.All"));
		}
		
	}

	/**
	 * Set the date to display in the panel
	 * @param time
	 */
	public void setDate (long time) 
	{
		this.datePanel.setDate(time);	
	}
	
	public void setStandardStructure (StandardStructure dd)
	{
		this.currentDate = (DailyDate) dd;
		this.setDate(currentDate.getDate().getTimeInMillis());
	}
	
	public StandardStructure getStandardStructure()
	{
		GregorianCalendar d = new GregorianCalendar();
		d.setTimeInMillis(this.datePanel.getDateInMillis());
		this.currentDate.setDate(d);
		return this.currentDate;
	}
	
	public int getNumberOfMaxDownloads()
	{
		// get number of episodes to download
		int number;
		Object value = episodeSpinner.getValue();
		if(value.toString().equals(Lang.getString("TedGeneral.All")))
		{
			number = 0;
		}
		else 
		{
			number = Integer.parseInt(value.toString());
		}
		
		return number;
	}

}
