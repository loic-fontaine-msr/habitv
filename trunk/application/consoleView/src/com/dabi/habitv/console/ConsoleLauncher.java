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

public final class ConsoleLauncher { // NO_UCD (unused code)

	private static final Logger LOG = Logger.getLogger(ConsoleLauncher.class);

	private static final String OPTION_PLUGIN = "p";

	private ConsoleLauncher() {

	}

	@SuppressWarnings("static-access")
	public static void main(final String[] args) {
		if (args.length > 0 && args[0].startsWith("http://")) {// FIXME
																// FrameworkConf.HTTP_PREFIX
			downloadEpisodes(args);
		} else {

			Options options = new Options();

			options.addOption("d", "deamon", false,
					"Lancement en mode démon avec scan automatique des épisodes à télécharger.");
			options.addOption("h", "checkAndDL", false,
					"Recherche des épisodes et lance les téléchargements.");
			options.addOption("u", "updateGrabConfig", false,
					"Met à jour le fichier des épisodes à télécharger.");
			options.addOption("k", "cleanGrabConfig", false,
					"Purge le fichier des épisodes à télécharger des catégories périmées.");
			options.addOption("le", "listEpisode", false,
					"Met à jour le fichier des épisodes à télécharger.");
			options.addOption("lc", "listCategory", false,
					"Recherche et liste les catégories des plugins.");
			options.addOption("t", "testPlugin", false,
					"Teste le plugin avec un téléchargement aléatoire.");
			options.addOption("x", "runExport", false,
					"Reprise des exports en échec.");

			options.addOption(OptionBuilder
					.withLongOpt("plugins")
					.hasArgs()
					.withValueSeparator()
					.withDescription(
							"Pour lister les plugins concernés par la commande, si vide tous les plugins le seront.")
					.create(OPTION_PLUGIN));			
			options.addOption(
					"c",
					"categories",
					false,
					"Pour lister les catégories concernées par la commande, si vide tous les catégories le seront.");
			options.addOption(OptionBuilder
					.withLongOpt("episodes")
					.hasArgs()
					.withValueSeparator()
					.withDescription(
							"Pour lister les identifiants (URL)  d'épisodes concernés par la commande, si vide tous les épisodes le seront.")
					.create("e"));			
			// options.addOption(
			// "e",
			// "episodes ",
			// false,
			// "Pour lister les identifiants (URL)  d'épisodes concernés par la commande, si vide tous les épisodes le seront.");
			options.addOption(OptionBuilder
					.withLongOpt("episodes")
					.hasArgs()
					.withValueSeparator()
					.withDescription(
							"Pour lister les identifiants (URL)  d'épisodes concernés par la commande, si vide tous les épisodes le seront.")
					.create("e"));

			// create the parser
			CommandLineParser parser = new BasicParser();
			// parse the command line arguments
			CommandLine line;
			try {
				line = parser.parse(options, args);
				List<String> episodeIdList = null;
				if (line.hasOption("e")) {
					episodeIdList = Arrays.asList(line.getOptionValues("e"));
				}

				System.out.println(episodeIdList);
			} catch (ParseException e) {
				usage(options);
			}

			// List<String> categorieIdList = null;
			// if (!categories.getValuesList().isEmpty()) {
			// categorieIdList = categories.getValuesList();
			// }
			//
			// List<String> pluginList = null;
			// if (!plugins.getValuesList().isEmpty()) {
			// pluginList = plugins.getValuesList();
			// }

			// System.out.println(categorieIdList);
			// System.out.println(pluginList);
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

	private static void downloadEpisodes(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void main2(String args[]) {
		Options options = new Options();
		options.addOption("n", "name", true, "[name] your name");
		options.addOption(OptionBuilder.withLongOpt("episodes").hasArgs()
				.withValueSeparator()
				.withDescription("use value for given property").create("e"));
		Option timeOption = new Option("t", false, "current time");
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

		if (cmd.hasOption("t")) {

			System.out.println("You have given argument is t");
			System.err.println("Date/Time: " + new java.util.Date());
		}

		if (cmd.hasOption("n")) {
			System.out.println("You have given argument is n");
			System.err.println("Nice to meet you: "
					+ Arrays.asList(cmd.getOptionValues('n')));
			System.err.println("size " + cmd.getOptionValues('n').length);
		}

		if (cmd.hasOption("e")) {
			System.out.println("You have given argument is e");
			System.err.println("Nice to meet you: "
					+ Arrays.asList(cmd.getOptionValues('e')));
			System.err.println("size " + cmd.getOptionValues('e').length);
		}

	}

	private static void usage(Options options) {

		// Use the inbuilt formatter class
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("habiTv", options);
	}
}
