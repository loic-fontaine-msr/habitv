package ted.ui.addshowdialog;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import ted.Lang;
import ted.datastructures.StandardStructure;


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
public class SubscribeOptionsPanel extends JPanel
								   implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private static final int DOWNLOAD_LATEST = 0;
	private static final int ONLY_SUBSCRIBE  = 1;
	private static final int SELECT_EPISODE  = 2;
	
	private final Font SMALL_FONT = new Font("Dialog",0,10);
	private final Font MEDIUM_FONT = new Font("Dialog",0,12);
	private final Font LARGE_FONT = new Font("Dialog",0,15);
	
	private ButtonGroup subscribeOptions;
	private int selectedOption = 0;
	private JLabel futureEpisodeLabel;
	private JLabel airedEpisodeLabel;
	private JLabel customEpisodeLabel;
	
	private JRadioButton lastAiredRadio;
	private JRadioButton nextEpisodeRadio;
	private JRadioButton customSelectRadio;
	
	private StandardStructure selectedStructure = null;
	private StandardStructure nextEpisode;
	private StandardStructure lastAiredEpisode;
	private JLabel airedEpisodeTitleLabel;
	private JLabel futureEpisodeTitleLabel;
	private JLabel customEpisodeTitleLabel;

	private AddShowDialog addShowDialog;

	private StandardStructure customStructure;
	
	private Canvas globalActivityCanvas;
	private Canvas customActivityCanvas;
	
	public SubscribeOptionsPanel()
	{
		
	}
	
	public SubscribeOptionsPanel(AddShowDialog addDialog)
	{
		initGUI();
		this.addShowDialog = addDialog;
	}
	
	private void initGUI()
	{
		String buttonString = Lang.getString("AddShowDialog.SubscribeOptions.LastAired");
		lastAiredRadio = new JRadioButton(buttonString);
		lastAiredRadio.setActionCommand("latest");
		lastAiredRadio.addActionListener(this);
		lastAiredRadio.setFont(this.LARGE_FONT);
				
		buttonString = Lang.getString("AddShowDialog.SubscribeOptions.NextEpisode");
		nextEpisodeRadio = new JRadioButton(buttonString);
		nextEpisodeRadio.setActionCommand("only");
		nextEpisodeRadio.addActionListener(this);
		nextEpisodeRadio.setFont(this.LARGE_FONT);
		
		buttonString = Lang.getString("AddShowDialog.SubscribeOptions.CustomEpisode");
		customSelectRadio = new JRadioButton(buttonString);
		customSelectRadio.setActionCommand("select");
		customSelectRadio.addActionListener(this);
		customSelectRadio.setFont(this.LARGE_FONT);
		
		subscribeOptions = new ButtonGroup();
		subscribeOptions.add(lastAiredRadio);
		subscribeOptions.add(nextEpisodeRadio);
		subscribeOptions.add(customSelectRadio);
		
		nextEpisodeRadio.setSelected(true);
		
		FormLayout thisLayout = new FormLayout(
				"max(p;10dlu), max(p;5dlu):grow, max(p;5dlu)", 
				"max(p;16dlu), max(p;0dlu), max(p;0dlu), 5dlu, max(p;16dlu), max(p;0dlu), max(p;0dlu), 5dlu, max(p;16dlu), max(p;16px), max(p;16px)");
		this.setLayout(thisLayout);
		
		futureEpisodeLabel = new JLabel();
		futureEpisodeLabel.setFont(this.SMALL_FONT);
		futureEpisodeLabel.setForeground(Color.DARK_GRAY);
		airedEpisodeLabel	= new JLabel();
		airedEpisodeLabel.setFont(this.SMALL_FONT);
		airedEpisodeLabel.setForeground(Color.DARK_GRAY);
		customEpisodeLabel	= new JLabel();
		customEpisodeLabel.setFont(this.SMALL_FONT);
		customEpisodeLabel.setForeground(Color.DARK_GRAY);
		
		this.clear();	
		getGlobalActivitySpinner().setVisible(false);
		getCustomActivitySpinner().setVisible(false);
		
		this.add(nextEpisodeRadio, new CellConstraints("1, 1, 2, 1, default, default"));
		this.add(getGlobalActivitySpinner(), new CellConstraints("1, 1, 1, 1, fill, fill"));
		this.add(futureEpisodeLabel, new CellConstraints("2, 3, 1, 1, default, default"));
		this.add(lastAiredRadio, new CellConstraints("1, 5, 2, 1, default, default"));
		this.add(airedEpisodeLabel, new CellConstraints("2, 7, 1, 1, default, default"));
		this.add(customSelectRadio, new CellConstraints("1, 9, 2, 1, default, default"));
		this.add(customEpisodeLabel, new CellConstraints("2, 11, 2, 1, default, default"));
		this.add(getCustomEpisodeTitleLabel(), new CellConstraints("2, 10, 2, 1, default, default"));
		this.add(getCustomActivitySpinner(), new CellConstraints("2, 10, 2, 1, default, default"));
		this.add(getFutureEpisodeTitleLabel(), new CellConstraints("2, 2, 2, 1, default, default"));
		this.add(getAiredEpisodeTitleLabel(), new CellConstraints("2, 6, 2, 1, default, default"));
	}
	
	public int getSelectedOption()
	{
		return selectedOption;
	}
	
	public StandardStructure getSelectedEpisode()
	{
		return this.selectedStructure;
	}

	public void actionPerformed(ActionEvent arg0) 
	{
		String action = arg0.getActionCommand();
		this.selectedStructure = null;
		if (action.equals("latest"))
		{
			selectedOption = DOWNLOAD_LATEST;
			this.selectedStructure = this.lastAiredEpisode;
			this.addShowDialog.setEpisodeChooserVisible(false);
			if (this.customStructure == null)
			{
				this.customEpisodeLabel.setText("");
			}
		}
		else if (action.equals("only"))
		{
			selectedOption = ONLY_SUBSCRIBE;
			this.selectedStructure = this.nextEpisode;
			this.addShowDialog.setEpisodeChooserVisible(false);
			if (this.customStructure == null)
			{
				this.customEpisodeLabel.setText("");
			}
		}
		else if  (action.equals("select"))
		{
			// show epchooser dialog
			selectedOption = SELECT_EPISODE;
			this.addShowDialog.setEpisodeChooserVisible(true);
			
			if (this.customStructure == null)
			{
				getCustomEpisodeTitleLabel().setText(Lang.getString("AddShowDialog.SubscribeOptions.CustomEpisodeSelect"));
			}
		}
		
		if (this.customStructure != null)
		{
			this.customEpisodeLabel.setText(this.customStructure.getSearchString());
		}
		
		this.addShowDialog.subscribeOptionChanged();
		
	}

	void clear()
	{
		// remove text for season/epsiodes and disable options
		lastAiredRadio.setEnabled(false);
		this.airedEpisodeLabel.setText("");
		this.getAiredEpisodeTitleLabel().setText("");
		nextEpisodeRadio.setEnabled(false);
		this.futureEpisodeLabel.setText("");
		this.getFutureEpisodeTitleLabel().setText("");
		customSelectRadio.setEnabled(false);
		this.customEpisodeLabel.setText("");
		this.getCustomEpisodeTitleLabel().setText("");
		this.getCustomEpisodeTitleLabel().setVisible(false);
		
		lastAiredRadio.setVisible(false);
		this.airedEpisodeLabel.setVisible(false);
		nextEpisodeRadio.setVisible(false);
		this.futureEpisodeLabel.setVisible(false);
		customSelectRadio.setVisible(false);
		this.customEpisodeLabel.setVisible(false);
		
		this.nextEpisode = null;
		this.lastAiredEpisode = null;
		this.customStructure = null;
		this.selectedStructure = null;
	}
	
	/**
	 * This method is used as callback when the user selects a custom structure from
	 * the episode selection panel.
	 * @param customStructure
	 */
	public void setCustomEpisode(StandardStructure customStructure)
	{
		if (customStructure != null)
		{
			this.selectedStructure = customStructure;
			this.customStructure = customStructure;
			// show in label
			
			// also set as selected in addshow dialog
			this.addShowDialog.subscribeOptionChanged();
			
			this.customEpisodeLabel.setText(customStructure.getSearchString());
			this.getCustomEpisodeTitleLabel().setText(customStructure.getSubscribtionOptionsTitle());
		}
	}

	public void setLastAiredEpisode(StandardStructure episode)
	{
		this.lastAiredEpisode = episode;
		this.airedEpisodeLabel.setText(	this.lastAiredEpisode.getSearchString());
		this.getAiredEpisodeTitleLabel().setText(this.lastAiredEpisode.getSubscribtionOptionsTitle());
		this.updateEnabledOptions();
	}
	
	public void enableCustomEpisodes()
	{
		// enable custom selection
		this.customSelectRadio.setEnabled(true);
	}

	public void setNextEpisode(StandardStructure nextEpisode2) 
	{
		if (nextEpisode2 != null)
		{
			this.nextEpisode = nextEpisode2;
			this.selectedStructure = nextEpisode;
			this.futureEpisodeLabel.setText( nextEpisode.getSearchString());
			this.getFutureEpisodeTitleLabel().setText(nextEpisode2.getSubscribtionOptionsTitle());
			this.updateEnabledOptions();
		}		
	}
	
	private void updateEnabledOptions()
	{
		// set visible
		lastAiredRadio.setVisible(true);
		this.airedEpisodeLabel.setVisible(true);
		nextEpisodeRadio.setVisible(true);
		this.futureEpisodeLabel.setVisible(true);
		customSelectRadio.setVisible(true);
		this.customEpisodeLabel.setVisible(true);
		this.getCustomEpisodeTitleLabel().setVisible(true);
		
		// enable/disable
		if (this.nextEpisode != null)
		{
			this.nextEpisodeRadio.setEnabled(true);
		}
		if (this.lastAiredEpisode != null)
		{
			this.lastAiredRadio.setEnabled(true);
		}
		
		// default value
		if (this.nextEpisode != null)
		{
			this.nextEpisodeRadio.setSelected(true);
		}
		else if (this.lastAiredEpisode != null)
		{
			this.lastAiredRadio.setSelected(true);
		}
		
		this.addShowDialog.subscribeOptionChanged();
	}
	
	private Canvas getGlobalActivitySpinner() {
		if (globalActivityCanvas == null) 
		{
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image activityIm = toolkit.getImage(getClass().getClassLoader().getResource("icons/activity.gif"));
			
			globalActivityCanvas = new ImageCanvas(activityIm.getSource());
			globalActivityCanvas.setPreferredSize(new java.awt.Dimension(16, 16));
			globalActivityCanvas.setVisible(false);
		}
		return globalActivityCanvas;
	}
	
	private Canvas getCustomActivitySpinner() {
		if (customActivityCanvas == null) 
		{
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image activityIm = toolkit.getImage(getClass().getClassLoader().getResource("icons/activity.gif"));
			
			customActivityCanvas = new ImageCanvas(activityIm.getSource());
			customActivityCanvas.setPreferredSize(new java.awt.Dimension(16, 16));
			customActivityCanvas.setVisible(false);
		}
		return customActivityCanvas;
	}
	
	/**
	 * When activity status is true, a activity icon is shown
	 * @param active
	 */
	public void setGlobalActivityStatus(boolean active)
	{
		getGlobalActivitySpinner().setVisible(active);
	}
	
	/**
	 * When activity status is true, a activity icon is shown
	 * @param active
	 */
	public void setCustomActivityStatus(boolean active)
	{
		getCustomActivitySpinner().setVisible(active);
	}
	
	private JLabel getCustomEpisodeTitleLabel() {
		if(customEpisodeTitleLabel == null) {
			customEpisodeTitleLabel = new JLabel();
			customEpisodeTitleLabel.setFont(this.MEDIUM_FONT);
			customEpisodeTitleLabel.setForeground(Color.DARK_GRAY);
		}
		return customEpisodeTitleLabel;
	}
	
	private JLabel getFutureEpisodeTitleLabel() {
		if(futureEpisodeTitleLabel == null) {
			futureEpisodeTitleLabel = new JLabel();
			futureEpisodeTitleLabel.setFont(this.MEDIUM_FONT);
			futureEpisodeTitleLabel.setForeground(Color.DARK_GRAY);
		}
		return futureEpisodeTitleLabel;
	}
	
	private JLabel getAiredEpisodeTitleLabel() {
		if(airedEpisodeTitleLabel == null) {
			airedEpisodeTitleLabel = new JLabel();
			airedEpisodeTitleLabel.setFont(this.MEDIUM_FONT);
			airedEpisodeTitleLabel.setForeground(Color.DARK_GRAY);
		}
		return airedEpisodeTitleLabel;
	}
}

