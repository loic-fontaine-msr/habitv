import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.provider.tvSubtitles.TvSubtitlesRetriever;

public class TestTvSubtitles {
	public static void main(String[] args) throws Exception {
		// TvSubtitlesCategoriesFinder.findCategory();
		TvSubtitlesRetriever.findEpisodeByCategory(new CategoryDTO("", "", "tvshow-29-8.html", ""));
	}

}
