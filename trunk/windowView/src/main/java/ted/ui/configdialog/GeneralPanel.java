package ted.ui.configdialog;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import ted.FormattedTimeZones;
import ted.Lang;
import ted.TedConfig;
import ted.TedLog;
import ted.TedSystemInfo;

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
public class GeneralPanel extends JPanel implements ActionListener
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JPanel generalPanel;
   private JCheckBox downloadToShowFolders;
   private JCheckBox downloadToSeasonFolders;
	private JLabel timeZoneLabel;
	private JComboBox timeZoneComboBox;
	private JSeparator jSeparator2;
	private JCheckBox checkParseAtStart;
	private JSeparator jSeparator3;
	private JCheckBox openTorrentBox;
	private JButton SaveDir_Button;
	private JTextField directory_Field;
	private JLabel saveLavel;
	private JLabel jLabel3;
	private JTextField textRefresh;
	private JLabel jLabel2;
	private JLabel Refresh_Label;
	private String directory_nieuw;

	public GeneralPanel()
	{
		this.initGUI();
	}

	private void initGUI()
	{
		try {
			{
				this.setPreferredSize(new java.awt.Dimension(510, 247));
			}
			generalPanel = new JPanel();
			this.add(generalPanel);
         FormLayout generalPanelLayout = new FormLayout(
                  "6dlu, 17dlu, max(p;6dlu), 31dlu:grow, max(p;6dlu), 30dlu, max(p;16dlu)", 
                  "5dlu, max(p;5dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;5dlu), max(p;5dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu)");
			generalPanel.setLayout(generalPanelLayout);
			generalPanel.setPreferredSize(new java.awt.Dimension(500, 500));
			generalPanel.setOpaque(false);

			Refresh_Label = new JLabel();
			generalPanel.add(Refresh_Label, new CellConstraints("2, 2, 5, 1, default, default"));
			Refresh_Label.setText(Lang.getString("TedConfigDialog.LabelRefreshTime"));
			Refresh_Label.setBounds(14, 10, 182, 28);

			jLabel2 = new JLabel();
			generalPanel.add(jLabel2, new CellConstraints("4, 3, 2, 1, default, default"));
			jLabel2.setText(Lang.getString("TedConfigDialog.RefreshTimeMinutes"));
			jLabel2.setBounds(276, 9, 60, 30);

			textRefresh = new JTextField();
			generalPanel.add(textRefresh, new CellConstraints("2, 3, 1, 1, default, default"));
			textRefresh.setBounds(193, 9, 73, 28);

			jLabel3 = new JLabel();
			generalPanel.add(jLabel3, new CellConstraints("2, 6, 5, 1, default, default"));
			jLabel3.setText(Lang.getString("TedConfigDialog.LabelTorrentFound"));
			jLabel3.setBounds(14, 47, 370, 30);

			saveLavel = new JLabel();
			generalPanel.add(saveLavel, new CellConstraints("2, 7, 5, 1, default, default"));
			saveLavel.setText(Lang.getString("TedConfigDialog.LabelSaveDirectory"));
			saveLavel.setBounds(14, 70, 273, 28);

			directory_Field = new JTextField();
			generalPanel.add(directory_Field, new CellConstraints("2, 8, 3, 1, default, default"));//, 3, 1"));
			directory_Field.setBackground(new java.awt.Color(240, 240, 240));
			directory_Field.setBounds(21, 98, 315, 28);
			directory_Field.setEditable(false);

			SaveDir_Button = new JButton();
			generalPanel.add(SaveDir_Button, new CellConstraints("6, 8, 1, 1, default, default"));
			SaveDir_Button.setActionCommand("open dir");
			SaveDir_Button.setIcon(new ImageIcon(getClass().getClassLoader()
				.getResource("icons/dir.png")));
			SaveDir_Button.setBounds(343, 98, 35, 28);
			SaveDir_Button.addActionListener(this);

			openTorrentBox = new JCheckBox();
			generalPanel.add(openTorrentBox, new CellConstraints("2, 9, 5, 1, default, default"));
			openTorrentBox.setText(Lang
				.getString("TedConfigDialog.CheckOpenTorrentInClient"));
			openTorrentBox.setBounds(56, 98, 371, 28);

			jSeparator3 = new JSeparator();
			generalPanel.add(jSeparator3, new CellConstraints("2, 5, 5, 1, default, default"));
			jSeparator3.setBounds(18, 159, 350, 7);

			checkParseAtStart = new JCheckBox();
			generalPanel.add(checkParseAtStart, new CellConstraints("2, 4, 5, 1, default, default"));
			checkParseAtStart.setText(Lang
				.getString("TedConfigDialog.ParseAtStart"));
			checkParseAtStart.setBounds(21, 175, 357, 21);
			{
				jSeparator2 = new JSeparator();
				generalPanel.add(jSeparator2, new CellConstraints("2, 12, 5, 1, default, default"));
			}
			{
				ComboBoxModel timeZoneComboBoxModel =
					new DefaultComboBoxModel(FormattedTimeZones.getDescriptionsArray());

				timeZoneComboBox = new JComboBox();

				generalPanel.add(timeZoneComboBox, new CellConstraints("2, 14, 5, 1, default, default"));
				timeZoneComboBox.setModel(timeZoneComboBoxModel);
			}
			{
				timeZoneLabel = new JLabel();
				generalPanel.add(timeZoneLabel, new CellConstraints("2, 13, 5, 1, default, default"));
				timeZoneLabel.setText(Lang.getString("TedConfigDialog.SelectTimeZone"));
			}
         {
            downloadToShowFolders = new JCheckBox();
            generalPanel.add(downloadToShowFolders, new CellConstraints("2, 10, 3, 1, default, default"));
            downloadToShowFolders.setText(Lang.getString("TedConfigDialog.DownloadToShowFolders"));
            downloadToShowFolders.setActionCommand("download show folders");
            downloadToShowFolders.addActionListener(this);
         }
         {
            downloadToSeasonFolders = new JCheckBox();
            generalPanel.add(downloadToSeasonFolders, new CellConstraints("3, 11, 3, 1, default, default"));
            downloadToSeasonFolders.setText(Lang.getString("TedConfigDialog.DownloadToSeasonFolders"));
         }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the values of the current config in the fields
	 */
	public void setValues()
	{
		// get values
		openTorrentBox.setSelected(TedConfig.getInstance().isOpenTorrent());
		checkParseAtStart.setSelected(TedConfig.getInstance().isParseAtStart());
		textRefresh.setText(TedConfig.getInstance().getRefreshTime()/60+"");
		downloadToShowFolders.setSelected(TedConfig.getInstance().isDownloadToShowFolders());
		downloadToSeasonFolders.setSelected(TedConfig.getInstance().isDownloadToSeasonFolders());
		
		downloadToSeasonFolders.setEnabled(downloadToShowFolders.isSelected());
		
		this.directory_nieuw = TedConfig.getInstance().getDirectory();
		directory_Field.setText(this.directory_nieuw);

		String timezoneName = TedConfig.getInstance().getTimezoneName();
		int index = FormattedTimeZones.getIndexOfTimezone(timezoneName);
		timeZoneComboBox.setSelectedIndex(index);
	}

	/**
	 * Check filled info
	 */
	public boolean checkValues()
	{
		boolean result = true;
		int newTime = 0;
		try
		{
			newTime = Integer.parseInt(textRefresh.getText())*60;
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(null, textRefresh.getText() + " " + Lang.getString("TedConfigDialog.DialogNoValidRefreshTime")); //$NON-NLS-1$
			return false;
		}

		if (directory_nieuw.equals(""))
		{

			result = false;
		}
		else
		{
			File f = new File(directory_nieuw);
			if (!f.isDirectory()) //$NON-NLS-1$
			{
				// dir does not exist. try to create it.
				result = f.mkdir();
			}
		}
		if (result == false)
		{
			JOptionPane.showMessageDialog(null, Lang.getString("TedConfigDialog.DialogSelectDirectory")); //$NON-NLS-1$
			directory_Field.setBackground(new java.awt.Color(255,100,100));
		}

		return result;
	}

	/**
	 * Save values from the dialog to the config file
	 */
	public void saveValues()
	{
		// get values
		boolean opentorrent = openTorrentBox.isSelected();
		boolean parseAtStart = checkParseAtStart.isSelected();
		int newTime = Integer.parseInt(textRefresh.getText())*60;

		String timezone = timeZoneComboBox.getSelectedItem().toString();
		String timezoneName = FormattedTimeZones.getTimeZone(timezone);
		TedConfig.getInstance().setTimezoneName(timezoneName);

		// write them to config
		TedConfig.getInstance().setRefreshTime(newTime);
		TedConfig.getInstance().setDirectory(directory_nieuw);
		TedConfig.getInstance().setOpenTorrent(opentorrent);
		TedConfig.getInstance().setParseAtStart(parseAtStart);
		TedConfig.getInstance().setDownloadToShowFolders(downloadToShowFolders.isSelected());
		TedConfig.getInstance().setDownloadToSeasonFolders(downloadToSeasonFolders.isSelected());
	}

	/**
	 * Show a directory chooser for the user to set the directory where ted has to
	 * store his torrents
	 */
	private void showDirectoryChooser()
	{
	    // bug: FS#281 - mac: torrents can only save to boot drive
	    // JFileChooser is crippled on the mac so we'll use the AWT FileDialog
	    // which uses the native OS file chooser.
	    // Unfortunately that doesn't work so well on Windows as there's no support
	    // for FilenameFilters on Windows.
	    // So for other OS's we'll just stick with JFileChooser which works fine there.

		String newDirectory = null;
		if (TedSystemInfo.osIsMac())
		{
		    // Apple's way of making life a little more difficult. This preference makes
		    // the dialog a directories only chooser.
            System.setProperty("apple.awt.fileDialogForDirectories", "true");
     	    JFrame frame = new JFrame();
   		    FileDialog fd = new FileDialog(frame,
   		         Lang.getString("TedConfigDialog.DirectoryChooserTitle"));

     		fd.setVisible(true);
            if (fd.getDirectory() != null)
            {
     	        newDirectory = fd.getDirectory();

                if (fd.getFile() != null)
                {
         	        newDirectory += fd.getFile();
                }
   	        }
   	        System.setProperty("apple.awt.fileDialogForDirectories", "false");
		}
		else
        {
		    JFileChooser fileChooser = new JFileChooser();
		    fileChooser.setDialogTitle(Lang.getString("TedConfigDialog.DirectoryChooserTitle")); //$NON-NLS-1$
		    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
            {
		        try
                {
                    newDirectory = fileChooser.getSelectedFile().getCanonicalPath();
                }
                catch (IOException e)
                {
                    TedLog.getInstance().error(e.getMessage());
                }
		    }
		}

		if (newDirectory != null)
		{
			this.directory_nieuw = newDirectory;

			directory_Field.setText(directory_nieuw);
			directory_Field.setBackground(new java.awt.Color(240,240,240));
		}
		else
		{
		    TedLog.getInstance().error(Lang.getString("TedConfigDialog.LogNoSelection")); //$NON-NLS-1$
		}

	}

	public void actionPerformed(ActionEvent e)
	{
		String action = e.getActionCommand();

		if(action.equals("open dir"))
		{
			this.showDirectoryChooser();
		} else if (action.equals("download show folders")) {
		   downloadToSeasonFolders.setEnabled(downloadToShowFolders.isSelected());
		}
	}
}
