package ted.ui.configdialog;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Locale;
import javax.swing.JButton;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import ted.Lang;
import ted.TedColorPicker;
import ted.TedConfig;
import ted.TedSystemInfo;
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
public class LooknFeelPanel extends JPanel implements ActionListener, MouseListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6042247710601857318L;
	private JPanel     lookFeelPanel;
	private JLabel     labelUpdate;
	private JCheckBox  checkLogToFile;
	private JCheckBox  checkAllowLogging;
	private JSeparator jSeparator1;
	private JCheckBox  checkStartMinimized;
	private JCheckBox  checkAlertNewTorrent;
	private JSeparator jSeparator4;
	private JCheckBox  checkAddSystemTray;
	private JComboBox  comboLanguages;
	private JSeparator jSeparator5;
	private JCheckBox  checkAlertErrors;
	private JLabel     labelLanguage;
	private Locale[]   locales = Lang.getAvailableLocales();
	private TedMainDialog tMain;
	private JButton colorPickerButton;
	private JSeparator jSeparator2;

	public LooknFeelPanel()
	{
		this.initGUI();
	}
	
	public LooknFeelPanel(TedMainDialog dialog)
	{
		this.tMain = dialog;
		this.initGUI();
	}
	
	private void initGUI() 
	{
		try 
		{

		lookFeelPanel = new JPanel();
		this.add(lookFeelPanel);
		FormLayout lookFeelPanelLayout = new FormLayout(
				"max(p;6dlu), 6dlu, 15dlu:grow, max(p;16dlu)", 
				"max(p;5dlu), max(p;15dlu), max(p;5dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), 17dlu, max(p;15dlu), max(p;15dlu), max(p;15dlu)");
		lookFeelPanel.setPreferredSize(new java.awt.Dimension(500, 334));
		lookFeelPanel.setSize(new java.awt.Dimension(500,500));
		lookFeelPanel.setLayout(lookFeelPanelLayout);

		labelLanguage = new JLabel();
		lookFeelPanel.add(labelLanguage, new CellConstraints("2, 13, 2, 1, default, default"));
		labelLanguage.setText(Lang.getString("TedConfigDialog.LabelLanguage"));
		labelLanguage.setBounds(231, 483, 371, 28);

		checkStartMinimized = new JCheckBox();
		lookFeelPanel.add(checkStartMinimized, new CellConstraints("3, 3, 1, 1, default, default"));
		checkStartMinimized.setText(Lang
			.getString("TedConfigDialog.CheckStartMinimized"));
		checkStartMinimized.setSelected(false);
		checkStartMinimized.setBounds(14, 164, 371, 21);

		checkAlertNewTorrent = new JCheckBox();
		lookFeelPanel.add(checkAlertNewTorrent, new CellConstraints("2, 5, 2, 1, default, default"));
		checkAlertNewTorrent.setText(Lang
			.getString("TedConfigDialog.CheckAlertNewTorrent"));
		//checkAlertNewTorrent.setBounds(14, 236, 371, 21);

		checkAlertErrors = new JCheckBox();
		lookFeelPanel.add(checkAlertErrors, new CellConstraints("2, 6, 2, 1, default, default"));
		checkAlertErrors
			.setText(Lang.getString("TedConfigDialog.CheckAlertError"));
		checkAlertErrors.setBounds(14, 257, 371, 21);

		jSeparator5 = new JSeparator();
		lookFeelPanel.add(jSeparator5, new CellConstraints("2, 7, 2, 1, default, default"));
		jSeparator5.setBounds(-28, 266, 350, 7);

		comboLanguages = new JComboBox();
		lookFeelPanel.add(comboLanguages, new CellConstraints("2, 14, 2, 1, default, default"));
		//comboLanguages.setSelectedIndex(toSelect);
		comboLanguages.setBounds(21, 530, 343, 28);

		checkAddSystemTray = new JCheckBox();
		lookFeelPanel.add(checkAddSystemTray, new CellConstraints("2, 2, 2, 1, default, default"));
		checkAddSystemTray.setText(Lang.getString("TedConfigDialog.CheckAddTray") + " ("
				+ Lang.getString("TedConfigDialog.NeedsReboot") + ")");
		checkAddSystemTray.addActionListener(this);
		checkAddSystemTray
			.setActionCommand("systray"); //$NON-NLS-1$

		jSeparator4 = new JSeparator();
		lookFeelPanel.add(jSeparator4, new CellConstraints("2, 4, 2, 1, default, default"));
		jSeparator4.setBounds(14, 45, 350, 7);
		{
			jSeparator1 = new JSeparator();
			lookFeelPanel.add(jSeparator1, new CellConstraints("2, 10, 2, 1, default, default"));
		}
		{
			checkAllowLogging = new JCheckBox();
			lookFeelPanel.add(checkAllowLogging, new CellConstraints("2, 8, 2, 1, default, default"));
			checkAllowLogging.setText(Lang.getString("TedLog.AllowLogging"));
		}
		{
			checkLogToFile = new JCheckBox();
			lookFeelPanel.add(checkLogToFile, new CellConstraints("2, 9, 2, 1, default, default"));
			checkLogToFile.setText(Lang.getString("TedLog.LogToFile"));
		}
		{
			labelUpdate = new JLabel();
			lookFeelPanel.add(labelUpdate, new CellConstraints("3, 15, 1, 1, default, default"));
			labelUpdate.setForeground(Color.BLUE);
			labelUpdate.setFont(new java.awt.Font("Dialog",1,12));
			labelUpdate.addMouseListener(this);
			labelUpdate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			labelUpdate.setText("<html><u>"+ Lang.getString("TedMainMenuBar.Help.LanguageUpdate")+"</u></html>");
		}
		{
			jSeparator2 = new JSeparator();
			lookFeelPanel.add(jSeparator2, new CellConstraints("2, 12, 2, 1, default, default"));
		}
		{
			colorPickerButton = new JButton();
			lookFeelPanel.add(colorPickerButton, new CellConstraints("2, 11, 2, 1, default, default"));
			colorPickerButton.setText(Lang.getString("TedConfigDialog.ChooseColors"));
			colorPickerButton.setActionCommand("Color");
			colorPickerButton.addActionListener(this);
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
		checkAlertNewTorrent.setSelected(TedConfig.getInstance().isShowHurray());
		checkAlertErrors.setSelected(TedConfig.getInstance().isShowErrors());
		
		checkAllowLogging.setSelected(TedConfig.getInstance().isAllowLogging());
		checkLogToFile.setSelected(TedConfig.getInstance().isLogToFile());
		
		// when the os ted runs on does not support a tray icon
		if (!TedSystemInfo.osSupportsJDICTray())
		{
			checkAddSystemTray.setEnabled(false);
			checkAddSystemTray.setSelected(false);
			
			checkStartMinimized.setEnabled(false);
			checkStartMinimized.setSelected(false);	
		}
		else
		{
			checkAddSystemTray.setSelected(TedConfig.getInstance().isAddSysTray());
			checkStartMinimized.setSelected(TedConfig.getInstance().isStartMinimized());
			
			if (!TedConfig.getInstance().isAddSysTray())
			{
				this.checkStartMinimized.setSelected(false);
				this.checkStartMinimized.setEnabled(false);
			}
			
			
		}
		
		
		// fill combo box with available languages, and select current language
		String currentLanguage = TedConfig.getInstance().getLocale().getDisplayLanguage();
		String currentCountry  = TedConfig.getInstance().getLocale().getDisplayCountry();
		
		int toSelect = 0;
		int nrOfLocales = locales.length - 1;
		for (int i = 0; i <= nrOfLocales; i++)
		{
			String country  = "";
			String language = locales[i].getDisplayLanguage();
			
			// If the next or the previous language is the same also add the country
			// to the display name. Check for array out of bounds.
			if (   (i != 0           && locales[i-1].getDisplayLanguage().equals(language))
				|| (i != nrOfLocales && locales[i+1].getDisplayLanguage().equals(language)))
			{
				country = locales[i].getDisplayCountry();
			}
			
			// Format the output string if the country is specified.
			if (!country.equals(""))
			{
				language += " (" + country + ")";
			}
			
			// Add the language to the combo box.
			comboLanguages.addItem(language);
			
			// Select the correct language. Take into account the country if needed.
			if (   locales[i].getDisplayLanguage().equals(currentLanguage)
				&& (locales[i].getDisplayCountry().equals(currentCountry) || country.equals("")))
			{
				toSelect = i;
			}
		}
		comboLanguages.setSelectedIndex(toSelect);
		
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
		boolean alertHurray = checkAlertNewTorrent.isSelected();
		boolean alertError = checkAlertErrors.isSelected();
		boolean startMinimized = checkStartMinimized.isSelected();
		boolean addSysTray = checkAddSystemTray.isSelected();
		boolean allowLog = checkAllowLogging.isSelected();
		boolean logToFile = checkLogToFile.isSelected();
		Locale languageSetting = locales[this.comboLanguages.getSelectedIndex()];
	
		TedConfig.getInstance().setShowHurray(alertHurray);
		TedConfig.getInstance().setShowErrors(alertError);
		TedConfig.getInstance().setStartMinimized(startMinimized);
		TedConfig.getInstance().setAddSysTray(addSysTray);
		TedConfig.getInstance().setAllowLogging(allowLog);
		TedConfig.getInstance().setLogToFile(logToFile);
		TedConfig.getInstance().setLocale(languageSetting);		
	}

	public void actionPerformed(ActionEvent arg0)
	{
		String command = arg0.getActionCommand();
		
		if (command.equals("systray"))
		{
			boolean isChecked = this.checkAddSystemTray.isSelected();
			
			if (!isChecked)
			{
				this.checkStartMinimized.setSelected(false);
			}
			
			this.checkStartMinimized.setEnabled(isChecked);
		}
		else if (command.equals("Color"))
		{
			// popup the color picker
			TedColorPicker colorPicker = new TedColorPicker(this.tMain);
			colorPicker.setVisible(true);
		}
		
	}

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
		tMain.openTranslationLink();
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
