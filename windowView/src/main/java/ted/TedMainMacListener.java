package ted;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;


/**
 * This class will provide actionhandlers for the mac menu items of ted
 * @author roel
 *
 */
public class TedMainMacListener extends ApplicationAdapter 
{
	private TedMainDialog main;
	/**
	 * Create new Mac UI Listener to handle UI events from MacOS X
	 * @param main Current ted main window
	 */
	public TedMainMacListener(TedMainDialog main)
	{
		Application app = new Application();
		app.addAboutMenuItem();
		app.setEnabledAboutMenu(true);
		app.addPreferencesMenuItem();
		app.setEnabledPreferencesMenu(true);
		this.main = main;
		app.addApplicationListener(this);	
	}
	
	/* (non-Javadoc)
	 * @see com.apple.eawt.ApplicationListener#handleAbout(com.apple.eawt.ApplicationEvent)
	 */
	public void handleAbout(ApplicationEvent event)
	{
		main.showAboutDialog();
		event.setHandled(true);
	}
	/* (non-Javadoc)
	 * @see com.apple.eawt.ApplicationListener#handleQuit(com.apple.eawt.ApplicationEvent)
	 */
	public void handleQuit(ApplicationEvent event)
	{
		main.quit();
	}
	/* (non-Javadoc)
	 * @see com.apple.eawt.ApplicationListener#handlePreferences(com.apple.eawt.ApplicationEvent)
	 */
	public void handlePreferences(ApplicationEvent event)
	{
		main.showPreferencesDialog();
	}

}
