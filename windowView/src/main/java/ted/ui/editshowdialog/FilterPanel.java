package ted.ui.editshowdialog;
import java.awt.Font;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import javax.swing.JCheckBox;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ted.Lang;
import ted.TedConfig;
import ted.TedSerie;

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
public class FilterPanel extends JPanel implements ChangeListener, TextListener, ActionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4433322536308265906L;
	private JLabel label_minSize;
	private JSpinner spinner_maxSize;
	private TextField keyword_text;
	private JLabel hdLabel;
	private JCheckBox downloadHDCheckbox;
	private JSeparator jSeparator2;
	private JLabel label_keywords;
	private JLabel label_keywords3;
	private JLabel label_keywords2;
	private JLabel label_keywords1;
	private JSeparator jSeparator1;
	private JLabel label_Seeders;
	private JSpinner spinner_minSeeders;
	private JCheckBox check_applyKeyWords;
	private JCheckBox check_applySeeders;
	private JCheckBox check_applyMaxSize;
	private JCheckBox check_applyMinSize;
	private JCheckBox check_applyHD;
	private JLabel label_seederFilters;
	private JSeparator jSeparator4;
	private JLabel label_mb2;
	private JLabel label_maxSize;
	private JLabel label_mb1;
	private JSpinner spinner_minSize;
	private JLabel label_sizeFilters;
	
	private SpinnerNumberModel maxSizeSpinnerModel = new SpinnerNumberModel();
	private SpinnerNumberModel minSizeSpinnerModel = new SpinnerNumberModel();
	private SpinnerNumberModel minSeedersSpinnerModel = new SpinnerNumberModel();
	
	private final Font SMALL_FONT = new Font("Dialog",0,10);
	
	public FilterPanel()
	{
		this.initUI();
	}

	private void initUI()
	{
		try 
		{
			FormLayout thisLayout = new FormLayout(
					"max(p;5dlu), max(p;0dlu), 61dlu, 40dlu, 5dlu, max(p;5dlu):grow, max(p;15dlu)", 
					"max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu), 5dlu, max(p;15dlu), max(p;15dlu), 5dlu, max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), 5dlu, max(p;15dlu), max(p;15dlu), max(p;15dlu)");
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(379, 299));
			{ 
				label_sizeFilters = new JLabel();
				this.add(label_sizeFilters, new CellConstraints("2, 2, 5, 1, default, default"));
				label_sizeFilters.setText(Lang.getString("TedEpisodeDialog.LabelSize2"));
			}
			{
				label_minSize = new JLabel();
				this.add(label_minSize, new CellConstraints("3, 3, 1, 1, default, default"));
				label_minSize.setText(Lang
					.getString("TedEpisodeDialog.LabelMinSize2"));
			}
			{
				spinner_minSize = new JSpinner();
				spinner_minSize.setModel(minSizeSpinnerModel);
				this.add(spinner_minSize, new CellConstraints("4, 3, 1, 1, default, default"));
			}
			{
				label_mb1 = new JLabel();
				this.add(label_mb1, new CellConstraints("6, 3, 1, 1, default, default"));
				label_mb1.setText(Lang
					.getString("TedEpisodeDialog.LabelMegaByte"));
			}
			{
				label_maxSize = new JLabel();
				this.add(label_maxSize, new CellConstraints("3, 4, 1, 1, default, default"));
				label_maxSize.setText(Lang
					.getString("TedEpisodeDialog.LabelMaxSize"));
			}
			{
				spinner_maxSize = new JSpinner();
				spinner_maxSize.setModel(maxSizeSpinnerModel);
				this.add(spinner_maxSize, new CellConstraints("4, 4, 1, 1, default, default"));
			}
			{
				label_mb2 = new JLabel();
				this.add(label_mb2, new CellConstraints("6, 4, 1, 1, default, default"));
				label_mb2.setText(Lang
					.getString("TedEpisodeDialog.LabelMegaByte"));
			}
			{
				jSeparator4 = new JSeparator();
				this.add(jSeparator4, new CellConstraints("2, 5, 5, 1, default, default"));
			}
			{
				label_seederFilters = new JLabel();
				this.add(label_seederFilters, new CellConstraints("2, 6, 5, 1, default, default"));
				label_seederFilters.setText(Lang
					.getString("TedEpisodeDialog.LabelSeeders2"));
			}
			{
				spinner_minSeeders = new JSpinner();
				spinner_minSeeders.setModel(minSeedersSpinnerModel);
				this.add(spinner_minSeeders, new CellConstraints("4, 7, 1, 1, default, default"));
			}
			{
				label_Seeders = new JLabel();
				this.add(label_Seeders, new CellConstraints("6, 7, 1, 1, default, default"));
				label_Seeders.setText(Lang
					.getString("TedEpisodeDialog.LabelSeeders"));
			}
			{
				jSeparator1 = new JSeparator();
				this.add(jSeparator1, new CellConstraints("2, 8, 5, 1, default, default"));
			}
			{
				label_keywords1 = new JLabel();
				this.add(label_keywords1, new CellConstraints("3, 10, 4, 1, default, default"));
				label_keywords1.setText(Lang
					.getString("TedEpisodeDialog.LabelKeywords"));
				label_keywords1.setFont(SMALL_FONT);
				}
			{
				label_keywords2 = new JLabel();
				this.add(label_keywords2, new CellConstraints("3, 11, 4, 1, default, default"));
				label_keywords2.setText(Lang
					.getString("TedEpisodeDialog.LabelKeywordsHelp2"));
				label_keywords2.setFont(SMALL_FONT);
			}
			{
				label_keywords3 = new JLabel();
				this.add(label_keywords3, new CellConstraints("3, 12, 5, 1, default, default"));
				label_keywords3.setText(Lang
					.getString("TedEpisodeDialog.LabelKeywordsHelp3"));
				label_keywords3.setFont(SMALL_FONT);
			}
			{
				label_keywords = new JLabel();
				this.add(label_keywords, new CellConstraints("3, 9, 1, 1, default, fill"));
				label_keywords.setText(Lang
					.getString("TedEpisodeDialog.LabelKeywords1"));
			}
			{
				keyword_text = new TextField();
				this.add(keyword_text, new CellConstraints("4, 9, 3, 1, default, fill"));
			}
			{
				jSeparator2 = new JSeparator();
				this.add(jSeparator2, new CellConstraints("2, 13, 5, 1, default, default"));
			}
			{
				downloadHDCheckbox = new JCheckBox();
				this.add(downloadHDCheckbox, new CellConstraints("3, 14, 5, 1, default, default"));
				downloadHDCheckbox.setText(Lang.getString("TedAddShowDialog.DownloadInHd"));
				downloadHDCheckbox.addActionListener(this);
				downloadHDCheckbox.setActionCommand("hd");
			}
			{
				hdLabel = new JLabel();
				this.add(hdLabel, new CellConstraints("3, 15, 4, 1, default, default"));
				hdLabel.setText(Lang.getString("TedEpisodeDialog.LabelHD") + " (" + 
						TedConfig.getInstance().getHDKeywords() + ")");
				hdLabel.setFont(SMALL_FONT);
			}
			{
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		check_applyMinSize = new JCheckBox();
		check_applyMaxSize = new JCheckBox();
		check_applySeeders = new JCheckBox();
		check_applyKeyWords = new JCheckBox();
		check_applyHD = new JCheckBox();
		
		Integer value = new Integer(0);
		this.maxSizeSpinnerModel.setMinimum(value);
		this.minSeedersSpinnerModel.setMinimum(value);
		this.minSizeSpinnerModel.setMinimum(value);
		
	}
	
	public void addKeywords(String key)
	{
		String oldKeywords = this.keyword_text.getText();
		String newKeywords;
		if(!oldKeywords.contains(key))
		{
			if(!oldKeywords.equals(""))
				newKeywords = "((" + oldKeywords + ")&" + key + ')';
			else 
				newKeywords = key;
			
			this.keyword_text.setText(newKeywords);
		}
	}

	public void setValues(TedSerie serie)
	{
		// convert to integers
		Integer minSize = new Integer (serie.getMinSize());
		Integer maxSize = new Integer (serie.getMaxSize());
		Integer minSeeders = new Integer (serie.getMinNumOfSeeders());
		
		spinner_minSize.setValue(minSize);
		spinner_maxSize.setValue(maxSize);
		spinner_minSeeders.setValue(minSeeders);
		keyword_text.setText(""+serie.getKeywords());
		downloadHDCheckbox.setSelected(serie.isDownloadInHD());		
	}

	public boolean checkValues() 
	{
		int min = this.minSizeSpinnerModel.getNumber().intValue();
		int max = this.maxSizeSpinnerModel.getNumber().intValue();
			
		if(min>max)
		{
			JOptionPane.showMessageDialog(null, Lang.getString("TedEpisodeDialog.DialogMinLargerThanMax")); //$NON-NLS-1$
			return false;
		}
		if(!checkBrackets(keyword_text.getText()))
		{
			JOptionPane.showMessageDialog(null, Lang.getString("TedEpisodeDialog.DialogBrackets")); //$NON-NLS-1$
			return false;
		}	
		return true;
	}
	
	private boolean checkBrackets(String s)
	{
		boolean result = false;
		int count = 0;
		char c;
		
		for(int i=0; i<s.length(); i++)
		{
			c = s.charAt(i);
			if(c=='(')
				count++;
			else if(c==')')
				count--;
		}
		
		if(count==0)
			result = true;
		
		return result;
	}

	public void saveValues(TedSerie currentSerie) 
	{
		if (this.checkValues())
		{
			int min = this.minSizeSpinnerModel.getNumber().intValue();
			int max = this.maxSizeSpinnerModel.getNumber().intValue();
			int minSeeders = this.minSeedersSpinnerModel.getNumber().intValue();
			currentSerie.setMinSize(min);
			currentSerie.setMaxSize(max);
			currentSerie.setKeywords(keyword_text.getText().toLowerCase());
			currentSerie.setMinNumOfSeeders(minSeeders);
			currentSerie.setDownloadInHD(downloadHDCheckbox.isSelected());
		}
		
	}

	public void setValues(TedSerie[] showsList) 
	{
		
		// add the checkboxes so that users can enable/disable values to be applied to all shows
		this.add(check_applyMinSize, new CellConstraints("2, 3, 1, 1, default, default"));
		this.add(check_applyMaxSize, new CellConstraints("2, 4, 1, 1, default, default"));
		this.add(check_applySeeders, new CellConstraints("2, 7, 1, 1, default, default"));
		this.add(check_applyKeyWords, new CellConstraints("2, 9, 1, 1, default, default"));
		this.add(check_applyHD, new CellConstraints("2, 14, 1, 1, default, default"));
	
		// check if one of the values is equal for all shows, then pre-populate
		// that value in the fields of the dialog
		if (showsList.length > 0)
		{		
			boolean minSizeEqual = true;
			boolean maxSizeEqual = true;
			boolean minSeedersEqual = true;
			boolean keywordsEqual = true;
			boolean hdEqual = true;
			
			int minSize = showsList[0].getMinSize();
			int maxSize = showsList[0].getMaxSize();
			int minSeeders = showsList[0].getMinNumOfSeeders();
			String keyWords = showsList[0].getKeywords();
			boolean useHD = showsList[0].isDownloadInHD();
			
			for (int i = 1; i < showsList.length; i++)
			{
				minSizeEqual = minSizeEqual && (minSize == showsList[i].getMinSize());
				maxSizeEqual = maxSizeEqual && (maxSize == showsList[i].getMaxSize());
				minSeedersEqual = minSeedersEqual && (minSeeders == showsList[i].getMinNumOfSeeders());
				keywordsEqual = keywordsEqual && (keyWords.equalsIgnoreCase(showsList[i].getKeywords()));
				hdEqual = hdEqual && (useHD == showsList[i].isDownloadInHD());
				
				if (!minSizeEqual && !maxSizeEqual && !minSeedersEqual && !keywordsEqual && !hdEqual)
				{
					break;
				}
			}
			
			if (minSizeEqual)
			{
				Integer minSizeInt = new Integer (minSize);
				this.spinner_minSize.setValue(minSizeInt);
			}
			if (maxSizeEqual)
			{
				Integer maxSizeInt = new Integer (maxSize);
				this.spinner_maxSize.setValue(maxSizeInt);
			}
			if (minSeedersEqual)
			{
				Integer minSeedersInt = new Integer (minSeeders);
				this.spinner_minSeeders.setValue(minSeedersInt);
			}
			if (keywordsEqual)
			{
				this.keyword_text.setText(keyWords);
			}
			if (hdEqual)
			{
				this.downloadHDCheckbox.setSelected(useHD);
			}
		}
		
		// add listeners after we set the values (otherwise an event will be thrown once we set the value :)
		this.maxSizeSpinnerModel.addChangeListener(this);
		this.minSizeSpinnerModel.addChangeListener(this);
		this.minSeedersSpinnerModel.addChangeListener(this);
		this.keyword_text.addTextListener(this);
		
	}

	public void saveValues(TedSerie[] showsList2) 
	{
		if (this.checkValues())
		{
			// apply the values of all checked fields to the whole showlist
			boolean applyMaxSize = this.check_applyMaxSize.isSelected();
			boolean applyMinSize = this.check_applyMinSize.isSelected();
			boolean applyMinSeeders = this.check_applySeeders.isSelected();
			boolean applyKeywords = this.check_applyKeyWords.isSelected();
			boolean applyDownloadHD = this.check_applyHD.isSelected();
			int min = this.minSizeSpinnerModel.getNumber().intValue();
			int max = this.maxSizeSpinnerModel.getNumber().intValue();
			int minSeeders = this.minSeedersSpinnerModel.getNumber().intValue();
			String keywords = this.keyword_text.getText();
			boolean useHD = this.downloadHDCheckbox.isSelected();
			
			for (int i = 0; i < showsList2.length; i++)
			{
				TedSerie currentSerie = showsList2[i];

				if (applyMaxSize)
				{
					currentSerie.setMaxSize(max);
				}
				else if (  applyDownloadHD
						&& currentSerie.isDownloadInHD() != useHD)
				{
					// Adjust the size settings for en/disabling the HD option.
					// Only do this for sizes which don't have been adjusted by
					// the user himself (or the ones which were adjusted when
					// this option was selected).
					if (useHD) currentSerie.setMaxSize(currentSerie.getMaxSize() * 2);
					else       currentSerie.setMaxSize(currentSerie.getMaxSize() / 2);
				}
				
				if (applyMinSize)
				{
					currentSerie.setMinSize(min);
				}
				else if (  applyDownloadHD
						&& currentSerie.isDownloadInHD() != useHD)
				{
					if (useHD) currentSerie.setMinSize(currentSerie.getMinSize() * 2);
					else       currentSerie.setMinSize(currentSerie.getMinSize() / 2);
				}
				
				if (applyMinSeeders)
				{
					currentSerie.setMinNumOfSeeders(minSeeders);
				}
				
				if (applyKeywords)
				{
					currentSerie.setKeywords(keywords);
				}
				
				if (applyDownloadHD)
				{
					currentSerie.setDownloadInHD(useHD);
				}
			}
		}		
	}

	public void stateChanged(ChangeEvent e) 
	{
		if (e.getSource() == this.maxSizeSpinnerModel)
		{
			this.check_applyMaxSize.setSelected(true);
		}
		if (e.getSource() == this.minSizeSpinnerModel)
		{
			this.check_applyMinSize.setSelected(true);
		}
		if (e.getSource() == this.minSeedersSpinnerModel)
		{
			this.check_applySeeders.setSelected(true);
		}	
	}

	public void textValueChanged(TextEvent e) 
	{
		if (e.getSource() == this.keyword_text)
		{
			this.check_applyKeyWords.setSelected(true);
		}	
	}

	public void actionPerformed(ActionEvent arg0) 
	{
		String command = arg0.getActionCommand();
		
		if (command.equals("hd"))
		{
			this.check_applyHD.setSelected(true);
			
			int minSize = minSizeSpinnerModel.getNumber().intValue();
			int maxSize = maxSizeSpinnerModel.getNumber().intValue();
			
			// If the user selected the option to download HD episodes
			// increase the size filters.
			if (downloadHDCheckbox.isSelected())
			{
				if (minSize > 0) minSizeSpinnerModel.setValue(minSize * 2);
				if (maxSize > 0) maxSizeSpinnerModel.setValue(maxSize * 2);
			}
			// Otherwise the user has deselected the option so half the
			// size filters
			else 
			{
				if (minSize > 0) minSizeSpinnerModel.setValue(minSize / 2);
				if (maxSize > 0) maxSizeSpinnerModel.setValue(maxSize / 2);				
			}
		}
	}

}
