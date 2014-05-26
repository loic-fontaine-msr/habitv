package com.dabi.habitv.console;

import java.text.ParseException;
import java.util.logging.Logger;

public final class ConsoleLauncher { // NO_UCD (unused code)

	private static final Logger LOG = Logger.getLogger(ConsoleLauncher.class);

	private ConsoleLauncher() {

	}

	public static void main(final String[] args) throws InterruptedException {
		try {
//			Options options = new Options();
//			Option logfile   = OptionBuilder.withArgName( "file" )
//                    .hasArg()
//                    .withDescription(  "use given file for log" )
//                    .create( "logfile" );			
//			
//			options.addOption(logfile);
			
//		    // create the parser
//		    CommandLineParser parser = new DefaultParser();
//		    try {
//		        // parse the command line arguments
//		        CommandLine line = parser.parse( options, args );
//		    }
//		    catch( ParseException exp ) {
//		        // oops, something went wrong
//		        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
//		    }		
			
//			// has the buildfile argument been passed?
//			if( line.hasOption( "buildfile" ) ) {
//			    // initialise the member variable
//			    this.buildfile = line.getOptionValue( "buildfile" );
//			}			
			
			final UserConfig config = XMLUserConfig.initConfig();

			final GrabConfigDAO grabConfigDAO = new GrabConfigDAO(HabitTvConf.GRABCONFIG_XML_FILE);
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
