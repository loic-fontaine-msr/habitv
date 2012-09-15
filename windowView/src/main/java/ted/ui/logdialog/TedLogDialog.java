package ted.ui.logdialog;

/****************************************************
 * IMPORTS
 ****************************************************/
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ted.Lang;
import ted.log.TorrentLogEntry;


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
 * TED: Torrent Episode Downloader (2005 - 2006)
 *
 * This is the logviewer window
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
public class TedLogDialog extends JDialog implements ActionListener
{
	/****************************************************
	 * GLOBAL VARIABLES
	 ****************************************************/
	private static final long serialVersionUID = -8661705723352441097L;
	private JScrollPane scroll;
	private LogTable logTable;
	private JPanel panel;
	private JButton clear;
	private JButton switchLog;
	private JButton download;
	private boolean isClosed;


	/****************************************************
	 * CONSTRUCTORS
	 ****************************************************/
	/**
	 * Constructs a TedLog window
	 */
	public TedLogDialog()
	{
		initGUI();
	}

	/****************************************************
	 * LOCAL METHODS
	 ****************************************************/
	private void initGUI()
	{
		try {

				this.isClosed = true;

				panel = new JPanel();
				clear = new JButton(Lang.getString("TedLog.Clear"));
				clear.addActionListener(this);
				clear.setActionCommand("clear");
				panel.add(clear);

				switchLog = new JButton(Lang.getString("TedLog.DetailedLog"));
				switchLog.addActionListener(this);
				switchLog.setActionCommand("switch");
				panel.add(switchLog);

				download = new JButton(Lang.getString("TedLog.Download"));
				download.addActionListener(this);
				download.setActionCommand("download");
				panel.add(download);
				//TODO when download of torrents available set this to true!!!
				download.setVisible(false);

				logTable = new LogTable(download);

				scroll = new JScrollPane(logTable);
				this.getContentPane().add(scroll, BorderLayout.CENTER);
				scroll.setVisible(true);

				this.getContentPane().add(panel, BorderLayout.SOUTH);

				this.setTitle(Lang.getString("TedLog.Name"));
				this.setSize(700, 400);

				this.addWindowListener(new WindowAdapter()
						{
							public void windowClosing(WindowEvent evt)
							{
								rootWindowClosing(evt);
							}
						}
					);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/****************************************************
	 * PUBLIC METHODS
	 ****************************************************/
    /**
     * cleans up this window when Root Window is closing
     */
	public void rootWindowClosing(WindowEvent e)
	{
		isClosed = true;
		this.setVisible(false);
	}

	/****************************************************
	 * GETTERS & SETTERS
	 ****************************************************/
	/**
	 * @return if the logwindow is closed
	 */
	public boolean getIsClosed()
	{
		return this.isClosed;
	}

	public void actionPerformed(ActionEvent arg0)
	{
		if(arg0.getActionCommand().equals("clear"))
		{
			logTable.getModel().clearEntries();
		}
		else if(arg0.getActionCommand().equals("switch"))
		{
			if(switchLog.getText().equals(Lang.getString("TedLog.DetailedLog")))
			{
				displayDetailedLog(true);
			}
			else
			{
				displayDetailedLog(false);
			}

		}
		else if(arg0.getActionCommand().equals("download")){
			downloadTorrent();
		}
	}

	public void displayLog(boolean display){
		if(display){
			isClosed = false;
			displayDetailedLog(false);
		}
		else{
			isClosed = true;
		}
		this.setVisible(display);
	}
	/**
	 * Display Detailed log Entries or summary log entires
	 * @param detailed
	 */
	private void displayDetailedLog(boolean detailed){
		if(detailed){
			switchLog.setText(Lang.getString("TedLog.SummaryLog"));
			logTable.getModel().setDisplayAll();
		}
		else{
			switchLog.setText(Lang.getString("TedLog.DetailedLog"));
			logTable.getModel().setDisplaySimple();
		}
	}
	/**
	 * download the selected torrent
	 */
	private void downloadTorrent(){

		 TorrentLogEntry entry = (TorrentLogEntry) logTable.getSelected();
		 String url = entry.getURL();
		 System.out.println(url);
		 //TODO download
	}
}
