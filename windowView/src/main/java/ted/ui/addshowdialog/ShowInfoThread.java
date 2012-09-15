package ted.ui.addshowdialog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;

import ted.Lang;
import ted.TedLog;
import ted.TedSerie;


/**
 * Thread that retrieves showinformation from TV.com website
 * @author roel
 *
 */
public class ShowInfoThread extends Thread
{
	private JEditorPane showInfoPane;
	private TedSerie currentSerie;
	String startHTML = "<html><font face=\"Arial, Helvetica, sans-serif\">";
	String endHTML = "</font></html>";
	private boolean done;

	public ShowInfoThread(JEditorPane jep, TedSerie ts)
	{
		showInfoPane = jep;
		currentSerie = ts;
		done = false;
	}

	public void run()
	{
		// loading...
		showInfoPane.setText(startHTML + Lang.getString("TedAddShowDialog.ShowInfo.LabelLoadingInfo") + endHTML);

		// when a show is selected
		if(currentSerie!=null && !done)
		{
			// and has a TV.com url
			if (currentSerie.getTVcom() != "" && currentSerie.getTVcom() != null)
			{
				URL url;
				try
				{
					String location = "showInfoUrl ?";// TODO TV SHOW info URL TedShowsConfig.getInstance().getShowInfoURL();

					if(!location.equals("")  && !done)
					{
						// open the showinfo url and display
						url = new URL(location+currentSerie.getTVcom());
						showInfoPane.setPage(url);
					}
					else
					{
						TedLog.getInstance().error("shows.xml file is corrupt");
					}
				}
				catch (MalformedURLException e)
				{
					// url malformed, display error
					showInfoPane.setText(startHTML + Lang.getString("TedAddShowDialog.ShowInfo.ErrorMalformedURL") + endHTML);
				}
				catch (IOException e)
				{
					// url not found, display error
					showInfoPane.setText(startHTML + Lang.getString("TedAddShowDialog.ShowInfo.ErrorIO") + endHTML);
				}
				catch (Exception e)
				{
					// unkown error, display error and print stacktrace
					showInfoPane.setText(startHTML + Lang.getString("TedAddShowDialog.ShowInfo.ErrorUnknown") + endHTML);
					e.printStackTrace();
				}
			}
			else
			{
				// no info url available
				showInfoPane.setText(startHTML + Lang.getString("TedAddShowDialog.ShowInfo.LabelNoInfo") + endHTML);
			}
		}

	}

	public void done()
	{
		done = true;
	}

}
