import com.dabi.habitv.downloader.http.HttpDownloadPluginManager;
import com.dabi.habitv.framework.plugin.api.CmdProgressionListener;
import com.dabi.habitv.framework.plugin.exception.DownloadFailedException;

public class TestHttpDl {

	/**
	 * @param args
	 * @throws DownloadFailedException
	 */
	public static void main(String[] args) throws DownloadFailedException {
		HttpDownloadPluginManager dl = new HttpDownloadPluginManager();
		dl.download("http://media21.justin.tv/archives/2012-5-2/highlight_316928550.flv", "highlight_316928550.flv", null, new CmdProgressionListener() {
			@Override
			public void listen(String progression) {
				System.err.println(progression);
			}
		});
	}
}
