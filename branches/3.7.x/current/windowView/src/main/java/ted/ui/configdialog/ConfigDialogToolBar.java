package ted.ui.configdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JToolBar;

import ted.ui.ToolBarButton;

public class ConfigDialogToolBar extends JToolBar implements ActionListener  
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ToolBarButton generalButton;
	private ToolBarButton looknfeelButton;
	private ToolBarButton advancedButton;
	private ToolBarButton updatesButton;
	private ToolBarButton networkButton;
	private ConfigDialog tedConfigDialog;

	/**
	 * ConfigDialog toolbar
	 * @param tcd
	 */
	public ConfigDialogToolBar(ConfigDialog tcd)
	{
		this.setFloatable(false);
		this.tedConfigDialog = tcd;
		this.makeToolBar();
	}
	
	public ConfigDialogToolBar()
	{
		this.makeToolBar();
	}
	
	/**
	 * Create the toolbar for the config dialog
	 */
	private void makeToolBar()
	{
		ButtonGroup toolBarButtons = new ButtonGroup();

	    generalButton = new ToolBarButton(this.tedConfigDialog.COMMANDGENERAL, this, "TedConfigDialog");
	    toolBarButtons.add(generalButton);
	    this.add(generalButton);
	    generalButton.setSelected(true);
		

	    looknfeelButton = new ToolBarButton(this.tedConfigDialog.COMMANDLOOKNFEEL, this, "TedConfigDialog");
	    toolBarButtons.add(looknfeelButton);
	    this.add(looknfeelButton);
	    
	    advancedButton = new ToolBarButton(this.tedConfigDialog.COMMANDADVANCED, this, "TedConfigDialog");
	    toolBarButtons.add(advancedButton);
	    this.add(advancedButton);
	    
	    updatesButton = new ToolBarButton(this.tedConfigDialog.COMMANDUPDATES, this, "TedConfigDialog");
	    toolBarButtons.add(updatesButton);
	    this.add(updatesButton);	
	    
	    networkButton = new ToolBarButton(this.tedConfigDialog.COMMANDNETWORK, this, "TedConfigDialog");
	    toolBarButtons.add(networkButton);
	    this.add(networkButton);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0)
	{
		String command = arg0.getActionCommand();	
		tedConfigDialog.showPanel(command);	
	}

}
