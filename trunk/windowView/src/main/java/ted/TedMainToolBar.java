package ted;

import javax.swing.JToolBar;

import ted.view.TedMainDialog;

/**
 * TED: Torrent Episode Downloader (2005 - 2007)
 * 
 * This is the toolbar holding the buttons for the mainwindow of ted
 * It contains 4 buttons
 * 	Add show
 * 	Edit show
 * 	Delete show
 * 	Parse
 * and provides functionality to enable/disable the buttons.
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
public class TedMainToolBar extends JToolBar
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6781755801501055730L;
	private TedMainToolBarButton btn_AddShow;
	private TedMainToolBarButton btn_Edit;
	private TedMainToolBarButton btn_Delete;
	private TedMainToolBarButton btn_Parse;
	
	private String stopCheckingIcon = "icons/TedMainDialog.stopsearching.png"; //$NON-NLS-1$
	private String startCheckingIcon = "icons/TedMainDialog.startsearching.png"; //$NON-NLS-1$
	
	/**
	 * DO NOT USE THIS CONSTRUCTOR!
	 * Only used for Jigloo UI editor
	 */
	public TedMainToolBar()
	{
		this.initButtons(null);
	}
	
	/**
	 * Create a new ted toolbar showing the buttons for the main dialog
	 * @param tMain TedMainDialog providing the actions for the toolbarbuttons
	 */
	public TedMainToolBar(TedMainDialog tMain)
	{
		// not floatable
		this.setFloatable(false);	
		this.initButtons(tMain);
	}
	
	/**
	 * Init the buttons for the toolbar
	 * @param tMain TedMainDialog providing the actions for the toolbarbuttons
	 */
	private void initButtons(TedMainDialog tMain)
	{
		btn_AddShow = new TedMainToolBarButton("icons/TedMainDialog.addshow32.png", 
				"TedMainDialog.ButtonAddShow",
				"TedMainDialog.ButtonToolTipAddShow",
				"New", tMain);
		
		btn_Delete = new TedMainToolBarButton("icons/TedMainDialog.deleteshow32.png",
				"TedMainDialog.ButtonDeleteShow",
				"TedMainDialog.ButtonToolTipDeleteShow",
				"Delete", tMain);
		
		btn_Edit = new TedMainToolBarButton("icons/TedMainDialog.editshow32.png",
				"TedMainDialog.ButtonEditShow",
				"TedMainDialog.ButtonToolTipEditShow",
				"Edit", tMain);	
		
		btn_Parse = new TedMainToolBarButton(this.startCheckingIcon,
				"TedMainDialog.ButtonCheckShows",
				"TedMainDialog.ButtonToolTipCheckShows",
				"Parse", tMain);
		
		this.add(btn_AddShow);		
		this.add(btn_Delete);
		this.add(btn_Edit);
		this.add(btn_Parse);
	}

	/**
	 * Set the status of the edit button
	 * @param statusEdit
	 */
	public void setEditButtonStatus(boolean statusEdit)
	{
		btn_Edit.setEnabled(statusEdit);
		
	}

	/**
	 * Set the status of the delete button
	 * @param statusDelete
	 */
	public void setDeleteButtonStatus(boolean statusDelete)
	{
		btn_Delete.setEnabled(statusDelete);
		
	}
	
	/**
	 * Set the status of the parse button
	 * @param b
	 */
	public void setParseButtonStatus(boolean b)
	{
		btn_Parse.setEnabled(b);		
	}

	/**
	 * Set the status of the whole toolbar to "parsing"
	 * Meaning the text and icon of the parse button will be updated and
	 * the delete button is disabled
	 */
	public void setParsing()
	{
		this.btn_Parse.setText(Lang.getString("TedMainDialog.ButtonStopChecking")); //$NON-NLS-1$
		this.btn_Parse.setIcon(this.stopCheckingIcon);
		this.btn_Parse.setActionCommand("stop parsing"); //$NON-NLS-1$	
		
		this.setDeleteButtonStatus(false);
	}

	/**
	 * Set the status of the whole toolbar to "idle"
	 * Meaning the text and icon of the parse button will be updated and
	 * the delete button is enabled
	 */
	public void setIdle()
	{
		this.setDeleteButtonStatus(true);	
		this.btn_Parse.setText(Lang.getString("TedMainDialog.ButtonCheckShows")); //$NON-NLS-1$
		this.btn_Parse.setIcon(this.startCheckingIcon);
		this.btn_Parse.setEnabled(true);
		
		this.btn_Parse.setActionCommand("Parse"); //$NON-NLS-1$
		
	}

	/**
	 * Method to refresh the texts on the buttons
	 */
	public void updateText()
	{
		btn_Parse.updateText();
		btn_Edit.updateText();
		btn_Delete.updateText();
		this.btn_AddShow.updateText();
		
	}

	/**
	 * Set the text on the parsebutton
	 * @param string
	 */
	public void setParseButtonText(String string)
	{
		btn_Parse.setText(string);
		
	}
	
	public void setAddButtonEnabled(boolean b)
	{
		this.btn_AddShow.setEnabled(b);
	}
}
