package ted.ui.addshowdialog;
import java.awt.Canvas;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import ted.datastructures.StandardStructure;
import ted.interfaces.EpisodeChooserListener;
import ted.renderer.TedTableProgressbarRenderer;

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
public class EpisodeChooserPanel extends JPanel
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8763021531797512857L;
	private EpisodesTable episodesTable;
	private JScrollPane episodesScrollPane;
	private EpisodesTableModel episodesTableModel = new EpisodesTableModel();

	private Canvas activityCanvas;
	TedTableProgressbarRenderer ttpr;
	
	private StandardStructure selectedStructure = null;
	private EpisodeChooserListener episodesChooserListener;
	private boolean active;

	/**
	 * Create a episode chooser panel
	 * @param ecld Listener for events on the table
	 */
	public EpisodeChooserPanel(EpisodeChooserListener ecld)
	{
		this.episodesChooserListener = ecld;
		this.active = false;
		this.initGUI();
	}
	
	/**
	 * Called when the user clicks in the table. Updates the selected episode and
	 * responds to double clicks
	 * @param evt
	 */
	public void episodesTableMouseClicked(MouseEvent evt)
	{
		this.tableSelectionChanged();
		
		// if double click, add the show with selected season/episode
		// beware, this panel is used in multiple dialogs. make sure they all implement the
		// callback function
		if (evt.getClickCount() > 1 && selectedStructure != null)
		{
			this.episodesChooserListener.doubleClickOnEpisodeList();
		}		
	}
	
	/**
	 * Called when the selection in the table is changed
	 */
	private void tableSelectionChanged() 
	{
		// getselected row
		int selectedRow = episodesTable.getSelectedRow();

		if (selectedRow >= 0)
		{
			// update the selected structure
			selectedStructure = episodesTableModel.getStandardStructureAt(selectedRow);
		
			// call event on ecld
			this.episodesChooserListener.episodeSelectionChanged();
		}	
	}

	private JTable getEpisodesTable() 
	{
		if (episodesTable == null) 
		{
			
			episodesTable = new EpisodesTable();
			episodesTable.setModel(episodesTableModel);
			
			episodesTable.setAutoCreateColumnsFromModel(true);
			episodesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			episodesTable.setEditingRow(0);
			
			//	disable horizontal lines in table
			episodesTable.setShowHorizontalLines(false);
			episodesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			episodesTable.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					episodesTableMouseClicked(evt);
				}});
			episodesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent arg0) {
					tableSelectionChanged();
					
				}

				});
			ttpr = new TedTableProgressbarRenderer(0, 100);
			ttpr.setStringPainted(false);
			
			EpisodesTableRenderer tr = new EpisodesTableRenderer();
			episodesTable.setDefaultRenderer(Object.class, tr);
			episodesTable.setRowHeight(episodesTable.getRowHeight()+5);
			
			episodesTable.setDefaultRenderer(JProgressBar.class, ttpr);
			
			// make sure first column (with episodes) is wider than the rest
			TableColumn	column;				
			column = episodesTable.getColumnModel().getColumn(0);
			column.setPreferredWidth(60);
	    	column.setMinWidth(150);
			column = episodesTable.getColumnModel().getColumn(2);
			column.setPreferredWidth(20);
	    	column.setMinWidth(20);
		}
		return episodesTable;
	}
	
	private JScrollPane getEpisodesScrollPane() 
	{
		if (episodesScrollPane == null) {
			episodesScrollPane = new JScrollPane();
			episodesScrollPane.setViewportView(getEpisodesTable());
		}
		return episodesScrollPane;
	}
	
	private void initGUI() {
		try {
			
			FormLayout thisLayout = new FormLayout(
				"25dlu:grow, 16px, 25dlu:grow",
				"max(p;15dlu), 30dlu:grow");
			this.setLayout(thisLayout);
			this.add(getEpisodesScrollPane(), new CellConstraints("1, 1, 3, 2, default, default"));
			this.add(getActivityCanvas(), new CellConstraints("2, 2, 1, 1, default, default"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add a vector of episodes to the table
	 * @param seasonEpisodes
	 */
	public void setSeasonEpisodes(Vector seasonEpisodes)
	{
		this.selectedStructure = null;
		this.episodesTableModel.setSeasonEpisodes(seasonEpisodes);	
		ttpr.setMaximum(this.episodesTableModel.getMaxQuality());
	}

	/**
	 * Clears the table
	 */
	public void clear()
	{
		this.activityCanvas.setVisible(false);
		this.selectedStructure = null;
		this.episodesTableModel.clear();	
	}
	
	private Canvas getActivityCanvas() {
		if (activityCanvas == null) 
		{
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image activityIm = toolkit.getImage(getClass().getClassLoader().getResource("icons/activity.gif"));
			
			activityCanvas = new ImageCanvas(activityIm.getSource());
			activityCanvas.setPreferredSize(new java.awt.Dimension(16, 16));
			activityCanvas.setBackground(this.episodesTable.getBackground());
		}
		return activityCanvas;
	}
	
	/**
	 * When activity status is true, a activity icon is shown
	 * @param active
	 */
	public void setActivityStatus(boolean active)
	{
		this.active = active;
		this.getActivityCanvas().setVisible(active);
	}

	/**
	 * @return selected episode in the list
	 */
	public StandardStructure getSelectedStructure() 
	{
		return this.selectedStructure;
	}

	public boolean getActivityStatus() 
	{
		return this.active;
	}
	
	public void selectEpisode()
	{
		
	}

	public void selectEpisode(int indexInTable) 
	{
		episodesTable.setRowSelectionInterval(indexInTable, indexInTable);
	}

	public void setNextEpisode(StandardStructure nextEpisode) 
	{
		// insert next episode in front of data
		if (nextEpisode != null)
		{
			this.episodesTableModel.addSeasonEpisode(nextEpisode, 0);	
		}
	}
}
