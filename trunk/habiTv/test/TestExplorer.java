import java.awt.Desktop;
import java.io.File;

public class TestExplorer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		try {
//			String command = "explorer.exe C:\\habiTv\\index";
//			Runtime r = Runtime.getRuntime();
//			Process process = r.exec(command);
//			process.waitFor();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		open("C:\\habiTv\\index");
		open("C:\\habiTv\\grabconfig.xml");
	}

	static void open(String toOpen) {
		if (toOpen == null)
			throw new NullPointerException();
		if (!Desktop.isDesktopSupported())
			return;
		Desktop desktop = Desktop.getDesktop();

		try {
			desktop.open(new File(toOpen));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
