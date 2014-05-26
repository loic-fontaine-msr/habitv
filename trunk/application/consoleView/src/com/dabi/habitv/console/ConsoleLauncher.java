package com.dabi.habitv.console;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import com.dabi.habitv.core.config.HabitTvConf;
import com.dabi.habitv.core.config.UserConfig;
import com.dabi.habitv.core.config.XMLUserConfig;
import com.dabi.habitv.core.dao.GrabConfigDAO;
import com.dabi.habitv.core.mgr.CoreManager;
import com.dabi.habitv.framework.plugin.utils.ProcessingThreads;

public final class ConsoleLauncher { // NO_UCD (unused code)

	private static final Logger LOG = Logger.getLogger(ConsoleLauncher.class);

	private ConsoleLauncher() {

	}

	public static void main(final String[] args) throws InterruptedException {
		try {
			Options options = new Options();
			Option logfile = OptionBuilder.withArgName("file").hasArg()
					.withDescription("use given file for log")
					.create("logfile");

			options.addOption(logfile);

			// create the parser
			CommandLineParser parser = new BasicParser();
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			// has the buildfile argument been passed?
			if (line.hasOption("buildfile")) {
				// initialise the member variable
				line.getOptionValue("buildfile");
			}

			final UserConfig config = XMLUserConfig.initConfig();

			final GrabConfigDAO grabConfigDAO = new GrabConfigDAO(
					HabitTvConf.GRABCONFIG_XML_FILE);
			final CoreManager coreManager = new CoreManager(config);
			if (config.updateOnStartup()) {
				coreManager.update();
			}

			Runtime.getRuntime().addShutdownHook(new Thread() {

				@Override
				public void run() {
					LOG.info("Interrupted, closing all treatments");
					coreManager.forceEnd();
					ProcessingThreads.killAllProcessing();
				}

			});

			if (grabConfigDAO.exist()) {
				grabConfigDAO.updateGrabConfig(coreManager.findCategory());
				if (config.getDemonCheckTime() == null) {
					coreManager.update();
					coreManager.retreiveEpisode(grabConfigDAO.load());
				} else {
					final long demonTime = config.getDemonCheckTime() * 1000L;
					// demon mode
					while (true) {
						coreManager.update();
						coreManager.retreiveEpisode(grabConfigDAO.load());
						Thread.sleep(demonTime);
					}
				}
			} else {
				LOG.info("Génération des catégories à télécharger");
				grabConfigDAO.saveGrabConfig(coreManager.findCategory());
			}
		} catch (final Exception e) {
			LOG.error("", e);
			System.exit(1);
		}
	}

}
