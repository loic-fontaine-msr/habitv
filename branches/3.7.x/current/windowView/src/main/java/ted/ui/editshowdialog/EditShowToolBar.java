package ted.ui.editshowdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JToolBar;

import ted.interfaces.PanelSwitcher;
import ted.ui.ToolBarButton;

public class EditShowToolBar extends JToolBar implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 293340525572515270L;
	private ToolBarButton generalButton;
	private ToolBarButton feedsButton;
	private ToolBarButton filterButton;
	private ToolBarButton scheduleButton;
	private PanelSwitcher panelDialog;
	
	/**
	 * ConfigDialog toolbar
	 * @param tcd
	 */
	public EditShowToolBar(EditShowDialog ed)
	{
		this.setFloatable(false);
		this.panelDialog = ed;
		this.makeToolBar(true);
	}
	
	public EditShowToolBar()
	{
		this.makeToolBar(true);
	}
	
	public EditShowToolBar(EditMultipleShowsDialog editMultipleShowsDialog) 
	{
		this.setFloatable(false);
		this.panelDialog = editMultipleShowsDialog;
		this.makeToolBar(false);
	}

	/**
	 * Create the toolbar for the config dialog
	 */
	private void makeToolBar(boolean singleShow)
	{
		ButtonGroup toolBarButtons = new ButtonGroup();
	    generalButton = new ToolBarButton(EditShowDialog.GENERALCOMMAND, this, "EditShowDialog");
	    toolBarButtons.add(generalButton);
	    this.add(generalButton);
	    generalButton.setSelected(true);	

	    feedsButton = new ToolBarButton(EditShowDialog.FEEDSCOMMAND, this, "EditShowDialog");
	    toolBarButtons.add(feedsButton);
	    this.add(feedsButton);
	    
	    filterButton = new ToolBarButton(EditShowDialog.FILTERCOMMAND, this, "EditShowDialog");
	    toolBarButtons.add(filterButton);
	    this.add(filterButton);
	    
	    scheduleButton = new ToolBarButton(EditShowDialog.SCHEDULECOMMAND, this, "EditShowDialog");
	    toolBarButtons.add(scheduleButton);
		this.add(scheduleButton);	
		
		if (!singleShow)
		{
			generalButton.setVisible(false);
			feedsButton.setVisible(false);
			scheduleButton.setVisible(false);			
			filterButton.setSelected(true);
		}
	}

	public void actionPerformed(ActionEvent arg0)
	{
		String command = arg0.getActionCommand();	
		panelDialog.showPanel(command);			
	}

}
