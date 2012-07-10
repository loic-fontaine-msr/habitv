import com.dabi.habitv.framework.plugin.exception.ExecutorFailedException;
import com.dabi.habitv.framework.plugin.utils.CmdExecutor;

public class TestCmdExecutor {

	/**
	 * @param args
	 * @throws ExecutorFailedException
	 */
	public static void main(String[] args) throws ExecutorFailedException {
		// (new
		// CmdExecutor("D:/apps/video/rtmpdump/rtmpdump.exe -r \"rtmp://geo2-vod-fms.canalplus.fr/ondemand/GEO2/1203/FOOTBALL_LIGA_120324_CAN_249135_video_HD.mp4\" -o D:\\divx\\\\canalPlusFootball-FOOTBALL-Liga_-_30�me_journ�e_Real_Madrid___5_-_1.mp4")).execute();
		(new CmdExecutor(
				"ffmpeg -y -i \"D:\\divx\\canalPlusFootball-FOOTBALL-Liga_-_30�me_journ�e_Real_Madrid___5_-_1.mp4\" -vtag DIVX -f avi -vcodec mpeg4 -b 2600000 -acodec mp2 -ab 192000 -ar 48000 -ac 2  \"D:\\divx\\regular\\canalPlusFootball-FOOTBALL-Liga_-_30�me_journ�e_Real_Madrid___5_-_1.avi\"",
				null)).execute();
	}
}
