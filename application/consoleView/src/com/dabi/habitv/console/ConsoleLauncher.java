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
			
			options.addOption( "a", "all", false, "do not hide entries starting with ." );
			options.addOption( "A", "almost-all", false, "do not list implied . and .." );
			
			
			Option deamon = OptionBuilder
					.withArgName("deamon")
					.withDescription(
							"Lancement en mode démon avec scan automatique des épisodes à télécharger.")
					.create("deamon");
			options.addOption(deamon);

			Option checkAndDL = OptionBuilder
					.withArgName("checkAndDL")
					.withDescription(
							"Recherche des épisodes et lance les téléchargements.")
					.create("checkAndDL");
			options.addOption(checkAndDL);

			Option updateGrabConfig = OptionBuilder
					.withArgName("updateGrabConfig")
					.withDescription(
							"Met à jour le fichier des épisodes à télécharger.")
					.create("updateGrabConfig");
			options.addOption(updateGrabConfig);

			Option cleanGrabConfig = OptionBuilder
					.withArgName("cleanGrabConfig")
					.withDescription(
							"Purge le fichier des épisodes à télécharger des catégories périmées.")
					.create("cleanGrabConfig");
			options.addOption(cleanGrabConfig);

			Option listEpisode = OptionBuilder.withArgName("listEpisode")
					.withDescription("Liste les épisodes d'une catégorie.")
					.create("listEpisode");
			options.addOption(listEpisode);

			Option listCategory = OptionBuilder
					.withArgName("listCategory")
					.withDescription(
							"Recherche et liste les catégories des plugins.")
					.create("listCategory");
			options.addOption(listCategory);

			Option checkplugin = OptionBuilder
					.withArgName("checkplugin")
					.withDescription(
							"Teste le plugin avec un téléchargement aléatoire.")
					.create("checkplugin");
			options.addOption(checkplugin);

			Option runExport = OptionBuilder.withArgName("runExport")
					.withDescription("Reprise des exports en échec.").create("runExport");
			options.addOption(runExport);

			Option plugins = OptionBuilder
					.withArgName("p")
					.hasOptionalArgs()
					.withDescription(
							"Pour lister les plugins concernés par la commande, si vide tous les plugins le seront.")
					.create("p");
			options.addOption(runExport);

			Option categories = OptionBuilder
					.withArgName("c")
					.hasOptionalArgs()
					.withDescription(
							"Pour lister les catégories concernées par la commande, si vide tous les catégories le seront.")
					.create("c");

			Option episodes = OptionBuilder
					.withArgName("e")
					.hasOptionalArgs()
					.withDescription(
							"Pour lister les identifiants (URL)  d'épisodes concernés par la commande, si vide tous les épisodes le seront.")
					.create("e");

			options.addOption(runExport);

			// create the parser
			CommandLineParser parser = new BasicParser();
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			List<String> episodeIdList = null;
			if (!episodes.getValuesList().isEmpty()) {
				episodeIdList = episodes.getValuesList();
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
