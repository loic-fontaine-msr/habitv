import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class PatternMatcher {

	private static final Logger log = Logger.getLogger(PatternMatcher.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// compilation de la regex
		// \\((\\d+\\.\\d)%\\)$
		// \((\d+\.\d)%\)$
		Pattern p = Pattern.compile("\\[.*\\((.*)\\%\\).*\\]");
		// creation dun moteur de recherche
		//17862.141 kB / 160.44 sec (79.1%)
		Matcher m = p.matcher("[#2 SIZE:0B/426.7MiB(43.5%) CN:0 SEED:0 SPD:0Bs]");
		// lancement de la recherche de toutes les occurrences
		boolean b = m.find();
		// si recherche fructueuse
		if (b) {
			log.info("Groupe x : " + m.group(m.groupCount()));
			// pour chaque groupe
			for (int i = 0; i <= m.groupCount(); i++) {
				// affichage de la sous-chaine capturee
				log.info("Groupe " + i + " : " + m.group(i));
			}
		}
	}

}
