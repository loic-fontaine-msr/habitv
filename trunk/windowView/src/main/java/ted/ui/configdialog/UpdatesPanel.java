package ted.ui.configdialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;

import ted.Lang;
import ted.TedConfig;
import ted.view.TedMainDialog;

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
public class UpdatesPanel extends JPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3242517655107348731L;
	private JPanel updatePanel;
	private JLabel labelUpdateShows;
	private JSeparator jSeparator1;
	private JLabel labelSyncShows;
	private JButton buttonCheckVersionTed;
	private JButton buttonUpdateShows;
	private JRadioButton radioSyncShowsAlways;
	private JRadioButton radioSyncShowsAsk;
	private JRadioButton radioSyncShowsNever;
	private JRadioButton radioUpdateShowsAlways;
	private JRadioButton radioUpdateShowsAsk;
	private JRadioButton radioUpdateShowsNever;
	private ButtonGroup updateShowsGroup;
	private ButtonGroup syncShowsGroup;
	private JCheckBox checkCheckUpdates;
	private TedMainDialog main;
	
	public UpdatesPanel()
	{
		this.initGUI();
	}
	
	public UpdatesPanel(TedMainDialog main)
	{
		this.main = main;
		this.initGUI();
	}

	private void initGUI() 
	{
		try 
		{

			//this.setPreferredSize(new Dimension(width, height));
		updatePanel = new JPanel();
		this.add(updatePanel);
		FormLayout updatePanelLayout = new FormLayout(
			"max(p;6dlu), center:60dlu, center:60dlu:grow, center:60dlu, max(p;16dlu)",
			"max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;10dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu)");
		updatePanel.setPreferredSize(new java.awt.Dimension(500, 500));
		updatePanel.setLayout(updatePanelLayout);

		checkCheckUpdates = new JCheckBox();
		updatePanel.add(checkCheckUpdates, new CellConstraints(
			"2, 2, 3, 1, default, default"));
		checkCheckUpdates.setText(Lang.getString("TedConfigDialog.CheckNewVersions"));
		checkCheckUpdates.setBounds(14, 185, 371, 21);

		labelUpdateShows = new JLabel();
		updatePanel.add(labelUpdateShows, new CellConstraints(
			"2, 5, 3, 1, default, default"));
		labelUpdateShows.setText(Lang
			.getString("TedConfigDialog.LabelNewShowDefinitions"));
		labelUpdateShows.setBounds(14, 287, 371, 28);

		radioUpdateShowsNever = new JRadioButton();
		updatePanel.add(radioUpdateShowsNever, new CellConstraints(
			"2, 6, 1, 1, default, default"));
		radioUpdateShowsNever.setText(Lang.getString("TedConfigDialog.Never"));
		radioUpdateShowsNever.setBounds(28, 306, 98, 28);
		

		radioUpdateShowsAsk = new JRadioButton();
		updatePanel.add(radioUpdateShowsAsk, new CellConstraints(
			"3, 6, 1, 1, default, default"));
		radioUpdateShowsAsk.setText(Lang.getString("TedConfigDialog.Ask"));
		radioUpdateShowsAsk.setBounds(140, 306, 84, 28);

		radioUpdateShowsAlways = new JRadioButton();
		updatePanel.add(radioUpdateShowsAlways, new CellConstraints(
			"4, 6, 1, 1, default, default"));
		radioUpdateShowsAlways.setText(Lang.getString("TedConfigDialog.Always"));
		radioUpdateShowsAlways.setBounds(231, 306, 105, 28);
		
		updateShowsGroup = new ButtonGroup();
		updateShowsGroup.add(radioUpdateShowsNever);
		updateShowsGroup.add(radioUpdateShowsAsk);
		updateShowsGroup.add(radioUpdateShowsAlways);

		radioSyncShowsNever = new JRadioButton();
		updatePanel.add(radioSyncShowsNever, new CellConstraints(
			"2, 10, 1, 1, default, default"));
		radioSyncShowsNever.setText(Lang.getString("TedConfigDialog.Never"));
		radioSyncShowsNever.setBounds(28, 347, 98, 28);

		radioSyncShowsAsk = new JRadioButton();
		updatePanel.add(radioSyncShowsAsk, new CellConstraints(
			"3, 10, 1, 1, default, default"));
		radioSyncShowsAsk.setText(Lang.getString("TedConfigDialog.Ask"));
		radioSyncShowsAsk.setBounds(140, 347, 84, 28);

		radioSyncShowsAlways = new JRadioButton();
		updatePanel.add(radioSyncShowsAlways, new CellConstraints(
			"4, 10, 1, 1, default, default"));
		radioSyncShowsAlways.setText(Lang.getString("TedConfigDialog.Always"));
		radioSyncShowsAlways.setBounds(231, 347, 105, 28);
		
		syncShowsGroup = new ButtonGroup();
		syncShowsGroup.add(radioSyncShowsNever);
		syncShowsGroup.add(radioSyncShowsAsk);
		syncShowsGroup.add(radioSyncShowsAlways);

		buttonCheckVersionTed = new JButton();
		updatePanel.add(buttonCheckVersionTed, new CellConstraints(
			"2, 3, 3, 1, default, default"));
		buttonCheckVersionTed.setText(Lang.getString("TedConfigDialog.ButtonCheckUpdates"));
		buttonCheckVersionTed.setActionCommand("checktedupdates");
		buttonCheckVersionTed.addActionListener(this);

		buttonUpdateShows = new JButton();
		updatePanel.add(buttonUpdateShows, new CellConstraints(
			"2, 7, 3, 1, default, default"));
		buttonUpdateShows.setText(Lang.getString("TedConfigDialog.ButtonCheckShowUpdates"));
		buttonUpdateShows.setActionCommand("checkshowupdates");
		buttonUpdateShows.addActionListener(this);

		labelSyncShows = new JLabel();
		updatePanel.add(labelSyncShows, new CellConstraints("2, 9, 3, 1, default, default"));
		labelSyncShows.setText(Lang.getString("TedConfigDialog.LabelUpdateShowDefinitions"));

		jSeparator1 = new JSeparator();
		updatePanel.add(jSeparator1, new CellConstraints("2, 4, 3, 1, default, default"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Set the values of the current config in the fields
	 */
	public void setValues()
	{
		// set values
		this.setSelectedButton(updateShowsGroup, TedConfig.getInstance().getAutoUpdateFeedList());
		this.setSelectedButton(syncShowsGroup, TedConfig.getInstance().getAutoAdjustFeeds());
		this.checkCheckUpdates.setSelected(TedConfig.getInstance().isCheckVersion());	
	}
	
	/**
	 * Check filled info
	 */
	public boolean checkValues()
	{	
		return true;
	}
	
	/**
	 * Save values from the dialog to the config file
	 * @param config
	 */
	public void saveValues()
	{
		// get values

		boolean checkVersion = checkCheckUpdates.isSelected();
		int updateShows = this.getSelectedButton(updateShowsGroup);
		int syncShows = this.getSelectedButton(syncShowsGroup);
		
		TedConfig.getInstance().setCheckVersion(checkVersion);
		TedConfig.getInstance().setAutoUpdateFeedList(updateShows);
		TedConfig.getInstance().setAutoAdjustFeeds(syncShows);
		
	}
	
	/**
	 * Get selected state from buttongroup
	 * @param group
	 * @return
	 */
	private int getSelectedButton(ButtonGroup group)
	{
		int i = 0;
		Enumeration buttons = group.getElements();
		JRadioButton temp;
		while (buttons.hasMoreElements())
		{
			temp = (JRadioButton)buttons.nextElement();
			if (temp.isSelected())
			{
				// check which state is selected and return it
				String text = temp.getText();
				if (text.equals(Lang.getString("TedConfigDialog.Never"))) //$NON-NLS-1$
					return TedConfig.NEVER;
				else if (text.equals(Lang.getString("TedConfigDialog.Ask"))) //$NON-NLS-1$
					return TedConfig.ASK;
				else if (text.equals((Lang.getString("TedConfigDialog.Always")))) //$NON-NLS-1$
					return TedConfig.ALWAYS;
			}
			else
				i++;
		}
		
		// default return value
		return 1;
	}
	/**
	 * Select button from the group
	 * @param group
	 * @param toSelect
	 */
	private void setSelectedButton(ButtonGroup group, int toSelect)
	{
		int i = 0;
		Enumeration buttons = group.getElements();
		JRadioButton temp;
		while (buttons.hasMoreElements())
		{
			temp = (JRadioButton)buttons.nextElement();
			String text = temp.getText();
			if ((text.equals(Lang.getString("TedConfigDialog.Never")) 	&& toSelect == TedConfig.NEVER) //$NON-NLS-1$
			||  (text.equals(Lang.getString("TedConfigDialog.Ask")) 	&& toSelect == TedConfig.ASK) //$NON-NLS-1$
			||  (text.equals(Lang.getString("TedConfigDialog.Always")) 	&& toSelect == TedConfig.ALWAYS)) //$NON-NLS-1$
			{
				temp.setSelected(true);
			}
			else
				temp.setSelected(false);
			i++;
		}
	}

	public void actionPerformed(ActionEvent arg0)
	{
		String command = arg0.getActionCommand();
		
		if (command.equals("checktedupdates"))
		{
			main.isNewTed(true);
		}
		else if (command.equals("checkshowupdates"))
		{
			main.isNewPredefinedShowsXML(true);
		}
		
	}

}
