package ted.ui.editshowdialog;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ted.BrowserLauncher;
import ted.Lang;
import ted.TedConfig;
import ted.TedSerie;
import ted.ui.addshowdialog.ImageCanvas;

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
public class SchedulePanel extends JPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8342065043462454211L;
	//WeekSchedulePanel wPanel;
	//BreakSchedulePanel bPanel;
	private JButton buttonOpenEpguides;
	private JButton buttonOpenTVRage;
	private JTextField textTVRage;
	private JLabel labelTVRage;
	private JTextField textEpguidesID;
	private JLabel labelEpGuides;
	private JCheckBox checkAutoSchedule;
	private JLabel labelRefresh;
	private JLabel labelRefreshNext;
	private JButton buttonRefreshNow;
	private TedSerie serie;
	private final Font SMALL_FONT = new Font("Dialog",0,10);
	private Canvas globalActivityCanvas;

	public SchedulePanel()
	{
		this.initUI();
	}

	private void initUI()
	{
		try 
		{
			FormLayout thisLayout = new FormLayout(
					"max(p;5dlu), 5dlu, max(p;15dlu), 5dlu, max(p;15dlu), max(p;15dlu):grow, max(p;15dlu), max(p;15dlu)", 
					"max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), 115dlu");
			this.setLayout(thisLayout);

			{
				checkAutoSchedule = new JCheckBox();
				this.add(checkAutoSchedule, new CellConstraints("2, 1, 5, 1, default, default"));
				checkAutoSchedule.setText(Lang.getString("TedEpisodeDialog.CheckAutoSchedule"));
				checkAutoSchedule.setActionCommand("autoupdate");
				checkAutoSchedule.addActionListener(this);
				checkAutoSchedule.setEnabled(TedConfig.getInstance().isUseAutoSchedule());
			}
			{
				{
					labelEpGuides = new JLabel();
					this.add(labelEpGuides, new CellConstraints("3, 2, 1, 1, default, default"));
					labelEpGuides.setText("Epguides ID");
				}
				{
					textEpguidesID = new JTextField();
					this.add(textEpguidesID, new CellConstraints("5, 2, 2, 1, default, default"));
				}
				{
					buttonOpenEpguides = new JButton();
					this.add(buttonOpenEpguides, new CellConstraints("7, 2, 1, 1, default, default"));
					buttonOpenEpguides.setText(Lang.getString("TedEpisodeDialog.ButtonOpen"));
					buttonOpenEpguides.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/EditShowDialog-feeds-open.png")));
					buttonOpenEpguides.setActionCommand("openepguides");
					buttonOpenEpguides.setBounds(205, 248, 70, 21);
					buttonOpenEpguides.addActionListener(this);
				}
				{
					labelTVRage = new JLabel();
					this.add(labelTVRage, new CellConstraints("3, 3, 1, 1, default, default"));
					labelTVRage.setText("TVRage ID");
				}
				{
					textTVRage = new JTextField();
					this.add(textTVRage, new CellConstraints("5, 3, 2, 1, default, default"));
					textTVRage.setText("jTextField1");
				}
				{
					buttonOpenTVRage = new JButton();
					this.add(buttonOpenTVRage, new CellConstraints("7, 3, 1, 1, default, default"));
					buttonOpenTVRage.setText(Lang.getString("TedEpisodeDialog.ButtonOpen"));
					buttonOpenTVRage.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/EditShowDialog-feeds-open.png")));
					buttonOpenTVRage.setActionCommand("opentvrage");
					buttonOpenTVRage.setBounds(205, 248, 70, 21);
					buttonOpenTVRage.addActionListener(this);
				}
				{
					labelRefresh = new JLabel();
					this.add(labelRefresh, new CellConstraints("3, 4, 5, 1, default, default"));
					labelRefresh.setFont(SMALL_FONT);
				}
				{
					labelRefreshNext = new JLabel();
					labelRefreshNext.setFont(SMALL_FONT);
					this.add(labelRefreshNext, new CellConstraints("3, 5, 5, 1, default, default"));
				}
				{
					buttonRefreshNow = new JButton();
					this.add(buttonRefreshNow, new CellConstraints("3, 6, 4, 1, left, default"));
					buttonRefreshNow.setText(Lang.getString("TedEpisodeDialog.RefreshSchedule"));
					buttonRefreshNow.setActionCommand("refreshschedule");
					buttonRefreshNow.addActionListener(this);
					
					this.add(getGlobalActivitySpinner(), new CellConstraints("7, 6, 1, 1, fill, fill"));
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void saveValues(TedSerie serie)
	{		
		serie.setUseAutoSchedule(this.checkAutoSchedule.isSelected());
		boolean epguidesChanged = serie.setEpguidesName(this.textEpguidesID.getText());
		
		if (epguidesChanged)
		{
			serie.refreshSchedule();
		}
	}
	
	public void setValues(TedSerie serie, boolean newShow)
	{
		this.serie = serie;
		
		this.checkAutoSchedule.setSelected(serie.isUseAutoSchedule());
		this.textEpguidesID.setText(serie.getEpguidesName());
		this.textTVRage.setText("NO TV RAGE");
		
		Date lastRefreshDate = serie.getScheduleLastUpdateDate();
		Date nextRefreshDate = serie.getScheduleNextUpdateDate();
		// Format the date
		if (lastRefreshDate != null)
		{
			String lastRefresh = this.formatDate(lastRefreshDate);
			this.labelRefresh.setText(Lang.getString("TedEpisodeDialog.ScheduleLastRefresh") + " "
					+ lastRefresh
					+ ".");
		}
		if (nextRefreshDate != null)
		{
			String nextRefresh = this.formatDate(nextRefreshDate);
			this.labelRefreshNext.setText(Lang.getString("TedEpisodeDialog.ScheduleNextRefresh") + " "
					+ nextRefresh
					+ ".");
		}
		
		this.updatePanels();
	}
	
	private String formatDate(Date date) 
	{
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);
		return df.format(date);
	}

	public boolean checkValues()
	{
		return true;
	}

	public void actionPerformed(ActionEvent e) 
	{
		String command = e.getActionCommand();
		if (command.equals("autoupdate"))
		{
			updatePanels();
		}
		else if (command.equals("openepguides"))
		{
			String epguidesid = this.textEpguidesID.getText();
			try
			{
				BrowserLauncher.openURL("http://www.epguides.com/" + epguidesid + "/");
				
			} 
			catch (MalformedURLException e1)
			{
				// show popup?
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		}		
		else if (command.equals("opentvrage"))
		{
			String tvrageid = this.textTVRage.getText();
			try
			{
				if (tvrageid == "" || tvrageid == null)
				{
					// Get epguides ID to open search page on tvrage
					String epguidesid = this.textEpguidesID.getText();
					BrowserLauncher.openURL("http://www.tvrage.com/search.php?search="+ epguidesid +"+&show_ids=1");
				}
				else
				{
					BrowserLauncher.openURL("http://www.tvrage.com/shows/id-" + tvrageid + "/");
				}
				
			} 
			catch (MalformedURLException e1)
			{
				// show popup?
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		else if (command.equals("refreshschedule"))
		{
			this.getGlobalActivitySpinner().setVisible(true);
			this.saveValues(serie);
			serie.refreshSchedule();
			this.setValues(serie, false);
			this.getGlobalActivitySpinner().setVisible(false);
		}
		
	}

	private void updatePanels()
	{
		boolean isAutoSchedule = checkAutoSchedule.isSelected();
		boolean autoScheduleGloballyEnabled = TedConfig.getInstance().isUseAutoSchedule();
		
		this.labelEpGuides.setEnabled(isAutoSchedule && autoScheduleGloballyEnabled);
		this.textEpguidesID.setEnabled(isAutoSchedule && autoScheduleGloballyEnabled);
		this.buttonOpenEpguides.setEnabled(isAutoSchedule && autoScheduleGloballyEnabled);
		this.labelTVRage.setEnabled(isAutoSchedule && autoScheduleGloballyEnabled);
		this.textTVRage.setEnabled(isAutoSchedule && autoScheduleGloballyEnabled);
		this.buttonOpenTVRage.setEnabled(isAutoSchedule && autoScheduleGloballyEnabled);
		
		this.labelRefresh.setEnabled(isAutoSchedule && autoScheduleGloballyEnabled);
		this.labelRefreshNext.setEnabled(isAutoSchedule && autoScheduleGloballyEnabled);
		this.buttonRefreshNow.setEnabled(isAutoSchedule && autoScheduleGloballyEnabled);
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
}
