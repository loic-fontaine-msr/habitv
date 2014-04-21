package ted.ui.configdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;

import ted.Lang;
import ted.TedConfig;

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
public class NetworkPanel  extends JPanel implements ActionListener
{
	private JPanel networkPanel;
	private JLabel labelTimeOut;
	private JSlider timeoutInSecondsSlider;

	private JCheckBox useProxy;
	private JTextField proxyHost;
	private JTextField proxyPort;
	private JCheckBox useAuthProxy;
	private JTextField proxyUsername;
	private JSeparator jSeparator1;
	private JPasswordField proxyPassword;
	private JLabel proxyUserNameLabel;
	private JLabel proxyPasswordLabel;
	private JLabel proxyHostLabel;
	private JLabel proxyPortLabel;
	
	private final int MINTIMEOUT = 1;
	private final int MAXTIMEOUT = 20;
	
	public NetworkPanel()
	{
		this.InitGUI();
	}
	
	private void InitGUI()
	{
		networkPanel = new JPanel();
		this.add(networkPanel);
		FormLayout networkPanelLayout = new FormLayout(
				"6dlu, 39dlu, max(p;6dlu), 31dlu:grow, 5dlu, max(p;15dlu), max(p;6dlu), 30dlu, max(p;16dlu)", 
				"5dlu, max(p;5dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu), max(p;15dlu)");
		networkPanel.setLayout(networkPanelLayout);
		networkPanel.setPreferredSize(new java.awt.Dimension(500, 500));
		networkPanel.setOpaque(false);
		

		labelTimeOut = new JLabel();
		networkPanel.add(labelTimeOut, new CellConstraints("2, 2, 4, 1, default, default"));
		labelTimeOut.setText(Lang.getString("TedConfigDialog.LabelTimeout"));
		labelTimeOut.setBounds(14, 381, 371, 28);
		

		timeoutInSecondsSlider = new JSlider(JSlider.HORIZONTAL, MINTIMEOUT, MAXTIMEOUT, 10);
		timeoutInSecondsSlider.setMajorTickSpacing(19);
		timeoutInSecondsSlider.setMinorTickSpacing(1);

		//Create the label table
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put( new Integer( MINTIMEOUT ),   new JLabel(MINTIMEOUT+"") );
		labelTable.put( new Integer( MAXTIMEOUT/2 ), new JLabel(Lang.getString("TedConfigDialog.Seconds")) );
		labelTable.put( new Integer( MAXTIMEOUT ),   new JLabel(MAXTIMEOUT+"") );
		timeoutInSecondsSlider.setLabelTable( labelTable );

		timeoutInSecondsSlider.setPaintTicks(true);
		timeoutInSecondsSlider.setPaintLabels(true);
		timeoutInSecondsSlider.setPaintTrack(true);
		timeoutInSecondsSlider.setSnapToTicks(true);

		networkPanel.add(timeoutInSecondsSlider, new CellConstraints("2, 3, 7, 1, fill, fill"));
		
		{
			useProxy = new JCheckBox();
			networkPanel.add(useProxy, new CellConstraints("2, 5, 4, 1, default, default"));
			useProxy.setText(Lang.getString("TedConfigDialog.UseProxy"));
			useProxy.addActionListener(this);
			useProxy.setActionCommand("useProxy");
		}
		{
			proxyHost = new JTextField();
			networkPanel.add(proxyHost, new CellConstraints("4, 6, 1, 1, default, default"));
			proxyHost.setEnabled(false);
		}
		{
			proxyPort = new JTextField();
			networkPanel.add(proxyPort, new CellConstraints("8, 6, 1, 1, default, default"));
			proxyPort.setEnabled(false);
		}
		{
			useAuthProxy = new JCheckBox();
			networkPanel.add(useAuthProxy, new CellConstraints("2, 7, 4, 1, default, default"));
			useAuthProxy.setEnabled(false);
			useAuthProxy.setText(Lang.getString("TedConfigDialog.UseAuthProxy"));
			useAuthProxy.addActionListener(this);
			useAuthProxy.setActionCommand("useAuthProxy");
		}
		{
			proxyUsername = new JTextField();
			networkPanel.add(proxyUsername, new CellConstraints("4, 8, 5, 1, default, default"));
			proxyUsername.setEnabled(false);
		}
		{
			proxyPassword = new JPasswordField();
			networkPanel.add(proxyPassword, new CellConstraints("4, 9, 5, 1, default, default"));
			proxyPassword.setEnabled(false);
		}
		{
			proxyHostLabel = new JLabel();
			networkPanel.add(proxyHostLabel, new CellConstraints("2, 6, 1, 1, default, default"));
			proxyHostLabel.setText(Lang.getString("TedConfigDialog.proxyHostLabel"));
		}
		{
			proxyPortLabel = new JLabel();
			networkPanel.add(proxyPortLabel, new CellConstraints("6, 6, 1, 1, default, default"));
			proxyPortLabel.setText(Lang.getString("TedConfigDialog.proxyPortLabel"));
		}
		{
			proxyUserNameLabel = new JLabel();
			networkPanel.add(proxyUserNameLabel, new CellConstraints("2, 8, 1, 1, default, default"));
			proxyUserNameLabel.setText(Lang.getString("TedConfigDialog.proxyUserNameLabel"));
		}
		{
			proxyPasswordLabel = new JLabel();
			networkPanel.add(proxyPasswordLabel, new CellConstraints("2, 9, 1, 1, default, default"));
			proxyPasswordLabel.setText(Lang.getString("TedConfigDialog.proxyPasswordLabel"));
		}
		{
			jSeparator1 = new JSeparator();
			networkPanel.add(jSeparator1, new CellConstraints("2, 4, 7, 1, default, default"));
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent arg0) 
	{
		String action = arg0.getActionCommand();
		if(action.equals("useProxy"))
		{
			this.updateProxyOptions();
		}
		else if(action.equals("useAuthProxy"))
		{
			this.updateUseAuthProxy();
		}		
	}
	
	/**
	 * Set the values of the current config in the fields
	 */
	public void setValues()
	{
		// get values
		int timeOut = TedConfig.getInstance().getTimeOutInSecs();
		if (timeOut < MINTIMEOUT)
		{
			timeOut = MINTIMEOUT;
		}
		else if (timeOut > MAXTIMEOUT)
		{
			timeOut = MAXTIMEOUT;
		}
		timeoutInSecondsSlider.setValue(timeOut);		

		this.useProxy.setSelected(TedConfig.getInstance().getUseProxy());
		this.useAuthProxy.setSelected(TedConfig.getInstance().getUseProxyAuth());
		this.actionPerformed(new ActionEvent(this.useProxy,0,"useProxy"));
		this.actionPerformed(new ActionEvent(this.useAuthProxy,0,"useAuthProxy"));
		this.proxyUsername.setText(TedConfig.getInstance().getProxyUsername());
		this.proxyPassword.setText(TedConfig.getInstance().getProxyPassword());
		this.proxyHost.setText(TedConfig.getInstance().getProxyHost());
		this.proxyPort.setText(TedConfig.getInstance().getProxyPort());
	}

	/**
	 * Save values from the dialog to the config file
	 */
	public void saveValues()
	{
		int newTime = timeoutInSecondsSlider.getValue();
		String pass = new String(this.proxyPassword.getPassword());
		
		TedConfig.getInstance().setTimeOutInSecs(newTime);
		TedConfig.getInstance().setUseProxy(this.useProxy.isSelected());
		TedConfig.getInstance().setUseProxyAuth(this.useAuthProxy.isSelected());
		TedConfig.getInstance().setProxyUsername(this.proxyUsername.getText());
		TedConfig.getInstance().setProxyPassword(pass);
		TedConfig.getInstance().setProxyHost(this.proxyHost.getText());
		TedConfig.getInstance().setProxyPort(this.proxyPort.getText());
	}
	
	/**
	 * Check filled info
	 */
	public boolean checkValues()
	{
		boolean result = true;
		
		return result;
	}

	
	private void updateProxyOptions() 
	{	
		this.updateUseAuthProxy();
		this.proxyHost.setEnabled(this.useProxy.isSelected());
		this.proxyPort.setEnabled(this.useProxy.isSelected());
		this.proxyHostLabel.setEnabled(this.useProxy.isSelected());
		this.proxyPortLabel.setEnabled(this.useProxy.isSelected());
	}

	private void updateUseAuthProxy()
	{
		this.useAuthProxy.setEnabled(this.useProxy.isSelected());
		if (!this.useProxy.isSelected())
		{
			this.useAuthProxy.setSelected(false);
		}
		this.updateAuthProxyInput();
		
	}

	private void updateAuthProxyInput() 
	{
		this.proxyUsername.setEnabled(this.useAuthProxy.isSelected());
		this.proxyPassword.setEnabled(this.useAuthProxy.isSelected());
		this.proxyUserNameLabel.setEnabled(this.useAuthProxy.isSelected());
		this.proxyPasswordLabel.setEnabled(this.useAuthProxy.isSelected());
	}
}
