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
		Pattern p = Pattern.compile("\\((\\d+\\.\\d)%\\)$");
		// cr�ation d�un moteur de recherche
		Matcher m = p.matcher("17862.141 kB / 160.44 sec (79.1%)");
		// lancement de la recherche de toutes les occurrences
		boolean b = m.find();
		// si recherche fructueuse
		if (b) {
			log.info("Groupe x : " + m.group(m.groupCount()));
			// pour chaque groupe
			for (int i = 0; i <= m.groupCount(); i++) {
				// affichage de la sous-cha�ne captur�e
				log.info("Groupe " + i + " : " + m.group(i));
			}
		}
	}

}
