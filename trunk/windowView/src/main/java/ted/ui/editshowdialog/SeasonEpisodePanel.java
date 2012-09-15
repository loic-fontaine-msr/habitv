package ted.ui.editshowdialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import ted.Lang;
import ted.datastructures.DailyDate;
import ted.datastructures.SeasonEpisode;
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
public class SeasonEpisodePanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 162104329797582752L;
	private JSpinner seasonSpinner;
	private JSpinner episodeSpinner;
	private JLabel labelEpisode;
	private JLabel labelSeason;
	private SeasonEpisode currentSeasonEpisode = new SeasonEpisode();

	private SpinnerNumberModel episodeSpinnerModel = new SpinnerNumberModel();
	private SpinnerNumberModel seasonSpinnerModel = new SpinnerNumberModel();
	
	public SeasonEpisodePanel ()
	{
		this.initGUI();
	}
	
	private void initGUI() 
	{
		try {
			{
				FormLayout thisLayout = new FormLayout(
					"max(p;5dlu), max(p;5dlu), 5dlu, max(p;5dlu), 5dlu, max(p;5dlu), 5dlu, max(p;15dlu)",
					"max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu)");
				this.setLayout(thisLayout);
				//this.setPreferredSize(new java.awt.Dimension(352, 53));
				{
					seasonSpinner = new JSpinner();
					this.add(seasonSpinner, new CellConstraints("4, 2, 1, 1, default, default"));
					seasonSpinner.setModel(seasonSpinnerModel);
					seasonSpinner.setPreferredSize(new java.awt.Dimension(62, 21));
				}
				{
					episodeSpinner = new JSpinner();
					this.add(episodeSpinner, new CellConstraints("8, 2, 1, 1, default, default"));
					episodeSpinner.setModel(episodeSpinnerModel);
					episodeSpinner.setPreferredSize(new java.awt.Dimension(62, 21));
				}
				{
					labelSeason = new JLabel();
					this.add(labelSeason, new CellConstraints("2, 2, 1, 1, default, default"));
					labelSeason.setText(Lang.getString("TedEpisodeDialog.LabelCurrentSeason"));
				}
				{
					labelEpisode = new JLabel();
					this.add(labelEpisode, new CellConstraints("6, 2, 1, 1, default, default"));
					labelEpisode.setText(Lang.getString("TedEpisodeDialog.LabelCurrentEpisode"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Integer value = new Integer(0);
		this.episodeSpinnerModel.setMinimum(value);
		this.seasonSpinnerModel.setMinimum(value);
	}

	/**
	 * Set current season and episode for show
	 * @param currentSeason
	 * @param currentEpisode
	 */
	public void setSeasonEpisode(int currentSeason, int currentEpisode) 
	{
		// convert to integers
		Integer episode = new Integer (currentEpisode);
		Integer season = new Integer (currentSeason);

		// set in spinner models
		this.episodeSpinner.setValue(episode);
		this.seasonSpinner.setValue(season);
		
	}

	/**
	 * @return Selected season
	 */
	public int getEpisode() 
	{
		return this.episodeSpinnerModel.getNumber().intValue();
	}
	
	/**
	 * @return Selected season
	 */
	public int getSeason() 
	{
		return this.seasonSpinnerModel.getNumber().intValue();
	}
	
	public void setStandardStructure (StandardStructure dd)
	{
		this.currentSeasonEpisode = (SeasonEpisode) dd;
		this.setSeasonEpisode(currentSeasonEpisode.getSeason(), currentSeasonEpisode.getEpisode());
	}
	
	public StandardStructure getStandardStructure()
	{
		this.currentSeasonEpisode.setEpisode(getEpisode());
		this.currentSeasonEpisode.setSeason(getSeason());
		return this.currentSeasonEpisode;
	}
}
