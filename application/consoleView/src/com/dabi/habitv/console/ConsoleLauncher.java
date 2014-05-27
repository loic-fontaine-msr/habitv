package com.dabi.habitv.console;

import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

//import com.dabi.habitv.framework.FrameworkConf;

public final class ConsoleLauncher { // NO_UCD (unused code)

	//private static final Logger LOG = Logger.getLogger(ConsoleLauncher.class);

	private ConsoleLauncher() {

	}

	@SuppressWarnings({ "static-access", "unchecked" })
	public static void main(final String[] args) throws InterruptedException,
			ParseException {
		if (args.length > 0 && args[0].startsWith("http://")) {//FIXME FrameworkConf.HTTP_PREFIX
			downloadEpisodes(args);
		} else {

			
			Options options = new Options();
			
			options.addOption( "d", "deamon", false, "Lancement en mode démon avec scan automatique des épisodes à télécharger." );
			options.addOption( "h", "checkAndDL", false, "Recherche des épisodes et lance les téléchargements." );
			options.addOption( "u", "updateGrabConfig", false, "Met à jour le fichier des épisodes à télécharger." );
			options.addOption( "k", "cleanGrabConfig", false, "Purge le fichier des épisodes à télécharger des catégories périmées." );
			options.addOption( "le", "listEpisode", false, "Met à jour le fichier des épisodes à télécharger." );
			options.addOption( "lc", "listCategory", false, "Recherche et liste les catégories des plugins." );
			options.addOption( "t", "testPlugin", false, "Teste le plugin avec un téléchargement aléatoire." );
			options.addOption( "x", "runExport", false, "Reprise des exports en échec." );
			
			options.addOption( "p", "plugins", false, "Pour lister les plugins concernés par la commande, si vide tous les plugins le seront.");
			options.addOption( "c", "categories", false, "Pour lister les catégories concernées par la commande, si vide tous les catégories le seront.");
			Options episodes = options.addOption( "e", "episodes ", false, "Pour lister les identifiants (URL)  d'épisodes concernés par la commande, si vide tous les épisodes le seront.");

			// create the parser
			CommandLineParser parser = new BasicParser();
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			List<String> episodeIdList = null;
			Option episodeOption = episodes.getOption("e");
			if (!episodeOption.getValuesList().isEmpty()) {
				episodeIdList = episodeOption.getValuesList();
			}

			List<String> categorieIdList = null;
			if (!categories.getValuesList().isEmpty()) {
				categorieIdList = categories.getValuesList();
			}

			List<String> pluginList = null;
			if (!plugins.getValuesList().isEmpty()) {
				pluginList = plugins.getValuesList();
			}

			System.out.println(episodeIdList);
			System.out.println(categorieIdList);
			System.out.println(pluginList);
			System.out.println(options);
		}
		

//		try {
//			final UserConfig config = XMLUserConfig.initConfig();
//
//			final GrabConfigDAO grabConfigDAO = new GrabConfigDAO(
//					HabitTvConf.GRABCONFIG_XML_FILE);
//			final CoreManager coreManager = new CoreManager(config);
//			if (config.updateOnStartup()) {
//				coreManager.update();
//			}
//
//			Runtime.getRuntime().addShutdownHook(new Thread() {
//
//				@Override
//				public void run() {
//					LOG.info("Interrupted, closing all treatments");
//					coreManager.forceEnd();
//					ProcessingThreads.killAllProcessing();
//				}
//
//			});
//
//			if (grabConfigDAO.exist()) {
//				grabConfigDAO.updateGrabConfig(coreManager.findCategory());
//				if (config.getDemonCheckTime() == null) {
//					coreManager.update();
//					coreManager.retreiveEpisode(grabConfigDAO.load());
//				} else {
//					final long demonTime = config.getDemonCheckTime() * 1000L;
//					// demon mode
//					while (true) {
//						coreManager.update();
//						coreManager.retreiveEpisode(grabConfigDAO.load());
//						Thread.sleep(demonTime);
//					}
//				}
//			} else {
//				LOG.info("Génération des catégories à télécharger");
//				grabConfigDAO.saveGrabConfig(coreManager.findCategory());
//			}
//		} catch (final Exception e) {
//			LOG.error("", e);
//			System.exit(1);
//		}
	}

	private static void downloadEpisodes(String[] args) {
		// TODO Auto-generated method stub

	}
}
