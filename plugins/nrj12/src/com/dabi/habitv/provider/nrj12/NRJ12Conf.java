package com.dabi.habitv.provider.nrj12;

import com.dabi.habitv.framework.FrameworkConf;

public interface NRJ12Conf {

	String NAME = "nrj12";

	String HOME_URL = "http://www.nrj12.fr";

	String REPLAY_URL = "http://r.nrj.fr";

	String DUMP_CMD = "-r \"#VIDEO_URL#\" -c 1935 -m 10 -w a502adc490f7e313b42045ac24449ca3c6420e485c1f4fddc924678fa5f41ba9 -x 351945 -o \"#FILE_DEST#\"";

	String EXTENSION = FrameworkConf.MP4;

	String ENCODING = FrameworkConf.UTF8;

}
