package ted.ui.editshowdialog;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;

import ted.Lang;
import ted.TedSerie;
import ted.TedSystemInfo;
import ted.datastructures.StandardStructure;
import ted.interfaces.EpisodeChooserListener;
import ted.ui.addshowdialog.EpisodeChooserPanel;
import ted.ui.addshowdialog.EpisodeParserThread;


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
public class EpisodeChooserDialog extends JDialog implements ActionListener, EpisodeChooserListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3196371120607378813L;
	private JButton cancelButton;
	private JButton okButton;
	private EpisodeChooserPanel episodeChooserPanel;
	private EditShowDialog editShowDialog;

	public EpisodeChooserDialog(EditShowDialog frame)
	{	
		this.setModal(true);
		FormLayout thisLayout = new FormLayout(
				"max(p;5dlu):grow, max(p;5dlu), max(p;5dlu)", 
				"5dlu:grow, 5dlu, 30dlu, 5dlu");
		getContentPane().setLayout(thisLayout);
		this.initGUI();
		
		editShowDialog = frame;
	}
	
	private void initGUI() 
	{
		int okButtonColumn = 3;
		int cancelButtonColumn = 2;
		
		if (TedSystemInfo.osIsWindows())
		{
			okButtonColumn = 2;
			cancelButtonColumn = 3;
		}
		try {
			{
				okButton = new JButton();
				getContentPane().add(okButton, new CellConstraints(okButtonColumn+", 3, 1, 1, default, default"));
				okButton.setText(Lang.getString("TedGeneral.ButtonOk"));
				//okButton.setBounds(280, 238, 98, 28);
				okButton.addActionListener(this);
				okButton.setActionCommand("ok");
				this.okButton.setEnabled(false);
				this.getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton();
				getContentPane().add(cancelButton, new CellConstraints(cancelButtonColumn+", 3, 1, 1, default, default"));
				cancelButton.setText(Lang.getString("TedGeneral.ButtonCancel"));
				//cancelButton.setBounds(165, 238, 100, 28);
				cancelButton.addActionListener(this);
				cancelButton.setActionCommand("cancel");
				
				episodeChooserPanel = new EpisodeChooserPanel(this);
				getContentPane().add(episodeChooserPanel, new CellConstraints("1, 1, 3, 1, fill, fill"));
				//episodeChooserPanel.setBounds(0, 0, 385, 231);
				
				
			}
			{
				this.setSize(500, 400);
				
//				Get the screen size
			    Toolkit toolkit = Toolkit.getDefaultToolkit();
			    Dimension screenSize = toolkit.getScreenSize();

			    //Calculate the frame location
			    int x = (screenSize.width - this.getWidth()) / 2;
			    int y = (screenSize.height - this.getHeight()) / 2;

			    //Set the new frame location
			    this.setLocation(x, y);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load the available episodes from the feeds of the show
	 * @param show
	 */
	public void loadEpisodes(TedSerie show)
	{
		EpisodeParserThread ept = new EpisodeParserThread(this.episodeChooserPanel, show);
		ept.setPriority( Thread.NORM_PRIORITY - 1 ); 
		
		ept.start();		
	}

	public void actionPerformed(ActionEvent arg0) 
	{
		String action = arg0.getActionCommand();
		
		if (action.equals("ok"))
		{
			this.confirmSelection();
		}
		else if (action.equals("cancel"))
		{
			// close dialog
			this.setVisible(false);
			this.dispose();
		}
		
	}

	/**
	 * Confirm selection of season/episode by user, give values to edit show dialog
	 */
	private void confirmSelection() 
	{
		StandardStructure selectedStructure = this.episodeChooserPanel.getSelectedStructure();
		// get selected episode and send it to the edit show dialog
		if (selectedStructure != null)
		{		
			editShowDialog.setEpisode(selectedStructure);
			
			this.setVisible(false);
			this.dispose();
		}
		else
		{
			//TODO: display error message? user has to select something
			// or let the episodeChooserPanel implement such a message
		}
		
	}

	public void episodeSelectionChanged() 
	{
		// if episode is selected
		if (this.episodeChooserPanel.getSelectedStructure() != null)
		{
			// enable ok button
			this.okButton.setEnabled(true);
		}
		else
		{
			this.okButton.setEnabled(false);
		}
		
	}

	public void doubleClickOnEpisodeList() 
	{
		this.confirmSelection();
		
	}

}
