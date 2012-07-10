import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestScoreRemover {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// compilation de la regex
		Pattern p = Pattern.compile(".*(\\d\\s*-\\s*\\d).*");
		// création d’un moteur de recherche
		String test= "Bordeaux 2 - 4 Paris Bordeaux 2 - 4 Paris";
		Matcher m = p.matcher(test);
		
		if (m.matches()){
			System.err.println(m.replaceFirst(""));
			test.replaceFirst("(\\d\\s*-\\s*\\d)", "");
			System.err.println(test);
		}

	}


}
