import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class TestExec {

	private static final Logger log = Logger.getLogger(TestExec.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			Runtime runtime = Runtime.getRuntime();
			final Process process = runtime.exec("cmd /c copy /Y D:\\mp3\\bibli\\loic\\techno\\* D:\\temp");
			process.destroy();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			try {
				while ((line = reader.readLine()) != null) {
					// Traitement du flux de sortie de l'application si besoin
					// est
					log.info(line);
				}
			} finally {
				reader.close();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
