package com.dabi.habitv.console;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

//import com.dabi.habitv.framework.FrameworkConf;

public final class ConsoleLauncher {
	private static final String OPTION_RUN_EXPORT = "x";

	private static final String OPTION_TEST_PLUGIN = "t";

	private static final String OPTION_LIST_PLUGIN = "lp";

	private static final String OPTION_LIST_CATEGORY = "lc";

	private static final String OPTION_LIST_EPISODE = "le";

	private static final String OPTION_CLEAN_GRABCONFIG = "k";

	private static final String OPTION_UPDATE_GRABCONFIG = "u";

	private static final String OPTION_CHECK_AND_DL = "h";

	private static final String OPTION_DAEMON = "d";

	private static final String OPTION_CATEGORY = "c";

	private static final String OPTION_EPISODE = "e";

	private static final String OPTION_PLUGIN = "p";

	private static final Logger LOG = Logger.getLogger(ConsoleLauncher.class);

	private ConsoleLauncher() {

	}

	@SuppressWarnings("static-access")
	public static void main(final String[] args) {
		if (args.length > 0 && args[0].startsWith("http://")) {// FIXME
																// FrameworkConf.HTTP_PREFIX
			downloadEpisodes(args);
		} else {

			Options options = new Options();

			options.addOption(OPTION_DAEMON, "deamon", false, "Lancement en mode démon avec scan automatique des épisodes à télécharger.");
			options.addOption(OPTION_CHECK_AND_DL, "checkAndDL", false, "Recherche des épisodes et lance les téléchargements.");
			options.addOption(OPTION_UPDATE_GRABCONFIG, "updateGrabConfig", false, "Met à jour le fichier des épisodes à télécharger.");
			options.addOption(OPTION_CLEAN_GRABCONFIG, "cleanGrabConfig", false,
					"Purge le fichier des épisodes à télécharger des catégories périmées.");
			options.addOption(OPTION_LIST_EPISODE, "listEpisode", false, "Met à jour le fichier des épisodes à télécharger.");
			options.addOption(OPTION_LIST_CATEGORY, "listCategory", false, "Recherche et liste les catégories des plugins.");
			options.addOption(OPTION_LIST_PLUGIN, "listPlugin", false, "Liste les plugins.");
			options.addOption(OPTION_TEST_PLUGIN, "testPlugin", false, "Teste le plugin avec un téléchargement aléatoire.");
			options.addOption(OPTION_RUN_EXPORT, "runExport", false, "Reprise des exports en échec.");

			options.addOption(OptionBuilder.withLongOpt("plugins").hasArgs().withValueSeparator()
					.withDescription("Pour lister les plugins concernés par la commande, si vide tous les plugins le seront.")
					.create(OPTION_PLUGIN));
			
			options.addOption(OptionBuilder.withLongOpt("categories").hasArgs().withValueSeparator()
					.withDescription("Pour lister les catégories concernées par la commande, si vide tous les catégories le seront.")
					.create(OPTION_CATEGORY));

			options.addOption(OptionBuilder
					.withLongOpt("episodes")
					.hasArgs()
					.withValueSeparator()
					.withDescription(
							"Pour lister les identifiants (URL)  d'épisodes concernés par la commande, si vide tous les épisodes le seront.")
					.create(OPTION_EPISODE));

			// create the parser
			CommandLineParser parser = new BasicParser();
			// parse the command line arguments
			CommandLine line;
			try {
				line = parser.parse(options, args);

				List<String> pluginList = null;
				if (line.hasOption(OPTION_PLUGIN)) {
					pluginList = Arrays.asList(line.getOptionValues(OPTION_PLUGIN));
				}

				List<String> categoryList = null;
				if (line.hasOption(OPTION_CATEGORY)) {
					categoryList = Arrays.asList(line.getOptionValues(OPTION_CATEGORY));
				}
//
//				List<String> episodeIdList = null;
//				if (line.hasOption(OPTION_EPISODE)) {
//					episodeIdList = Arrays.asList(line.getOptionValues(OPTION_EPISODE));
//				}

				if (line.hasOption(OPTION_DAEMON)) {
					daemonMode();
				} else if (line.hasOption(OPTION_CHECK_AND_DL)) {
					checkAndDLMode(pluginList, categoryList);
				} else if (line.hasOption(OPTION_UPDATE_GRABCONFIG)) {
					updateGrabConfig(pluginList);
				} else if (line.hasOption(OPTION_CLEAN_GRABCONFIG)) {
					updateGrabConfig(pluginList);
				} else if (line.hasOption(OPTION_LIST_EPISODE)) {
					listEpisode(pluginList, categoryList);
				} else if (line.hasOption(OPTION_LIST_CATEGORY)) {
					listCategory(pluginList);
				} else if (line.hasOption(OPTION_LIST_PLUGIN)) {
					listPlugin(pluginList);
				} else if (line.hasOption(OPTION_TEST_PLUGIN)) {
					listPlugin(pluginList);
				} else if (line.hasOption(OPTION_RUN_EXPORT)) {
					runExport(pluginList);
				}

			} catch (ParseException e) {
				usage(options);
			}
		}

		// try {
		// final UserConfig config = XMLUserConfig.initConfig();
		//
		// final GrabConfigDAO grabConfigDAO = new GrabConfigDAO(
		// HabitTvConf.GRABCONFIG_XML_FILE);
		// final CoreManager coreManager = new CoreManager(config);
		// if (config.updateOnStartup()) {
		// coreManager.update();
		// }
		//
		// Runtime.getRuntime().addShutdownHook(new Thread() {
		//
		// @Override
		// public void run() {
		// LOG.info("Interrupted, closing all treatments");
		// coreManager.forceEnd();
		// ProcessingThreads.killAllProcessing();
		// }
		//
		// });
		//
		// if (grabConfigDAO.exist()) {
		// grabConfigDAO.updateGrabConfig(coreManager.findCategory());
		// if (config.getDemonCheckTime() == null) {
		// coreManager.update();
		// coreManager.retreiveEpisode(grabConfigDAO.load());
		// } else {
		// final long demonTime = config.getDemonCheckTime() * 1000L;
		// // demon mode
		// while (true) {
		// coreManager.update();
		// coreManager.retreiveEpisode(grabConfigDAO.load());
		// Thread.sleep(demonTime);
		// }
		// }
		// } else {
		// LOG.info("Génération des catégories à télécharger");
		// grabConfigDAO.saveGrabConfig(coreManager.findCategory());
		// }
		// } catch (final Exception e) {
		// LOG.error("", e);
		// System.exit(1);
		// }
	}

	private static void runExport(List<String> pluginList) {
		LOG.info("runExport : " + pluginList);
	}

	private static void listPlugin(List<String> pluginList) {
		LOG.info("listPlugin : " + pluginList);
	}

	private static void listCategory(List<String> pluginList) {
		LOG.info("listCategory : " + pluginList);
	}

	private static void listEpisode(List<String> pluginList, List<String> categoryList) {
		LOG.info("listEpisode : " + pluginList + " / " + categoryList);
	}

	private static void updateGrabConfig(List<String> pluginList) {
		LOG.info("updateGrabConfig : " + pluginList);
	}

	private static void checkAndDLMode(List<String> pluginList, List<String> categoryList) {
		LOG.info("checkAndDLMode" + pluginList + " / " + categoryList);
	}

	private static void daemonMode() {
		LOG.info("daemonMode");

	}

	private static void downloadEpisodes(String[] args) {
		LOG.info("downloadEpisodes" + Arrays.asList(args));
	}

	public static void main2(String args[]) {
		Options options = new Options();
		options.addOption("n", "name", true, "[name] your name");
		options.addOption(OptionBuilder.withLongOpt("episodes").hasArgs().withValueSeparator()
				.withDescription("use value for given property").create(OPTION_EPISODE));
		Option timeOption = new Option(OPTION_TEST_PLUGIN, false, "current time");
		options.addOption(timeOption);

		// ** now lets parse the input
		CommandLineParser parser = new BasicParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException pe) {
			usage(options);
			return;
		}

		// ** now lets interrogate the options and execute the relevant parts

		if (cmd.hasOption(OPTION_TEST_PLUGIN)) {

			System.out.println("You have given argument is t");
			System.err.println("Date/Time: " + new java.util.Date());
		}

		if (cmd.hasOption("n")) {
			System.out.println("You have given argument is n");
			System.err.println("Nice to meet you: " + Arrays.asList(cmd.getOptionValues('n')));
			System.err.println("size " + cmd.getOptionValues('n').length);
		}

		if (cmd.hasOption(OPTION_EPISODE)) {
			System.out.println("You have given argument is e");
			System.err.println("Nice to meet you: " + Arrays.asList(cmd.getOptionValues('e')));
			System.err.println("size " + cmd.getOptionValues('e').length);
		}

	}

	private static void usage(Options options) {

		// Use the inbuilt formatter class
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("habiTv", options);
	}
}
