package ted.ui;

import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import ted.Lang;

public class ToolBarButton extends JToggleButton
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6220006853963215634L;

	public ToolBarButton(String name, ActionListener parent, String parentname)
	{
		//JToggleButton button = new JToggleButton();
		this.setActionCommand(name);
		this.addActionListener(parent);
		
		//Set the image or, if that's invalid, equivalent text.
		ImageIcon icon = createImageIcon("icons/"+parentname+"-"+name+".png");
		if (icon != null) 
		{
			this.setIcon(icon);
			//button.setToolTipText("The " + name + " crayon");
			
		} 
		this.setText(Lang.getString(parentname+".ToolBar."+name));
		this.setRolloverEnabled(true);
		this.setVerticalTextPosition(JButton.BOTTOM);
		this.setHorizontalTextPosition(JButton.CENTER);
		//button.setBorder(null);
		this.setIconTextGap(3);
		//button.setOpaque(true);
		//button.setBorderPainted(false);
		//this.setBackground(Color.BLACK);

	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
    protected ImageIcon createImageIcon(String path) 
    {
    	
       java.net.URL imgURL = getClass().getClassLoader().getResource(path);
        if (imgURL != null) 
        {
            return new ImageIcon(imgURL);
        } 
        else 
        {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
