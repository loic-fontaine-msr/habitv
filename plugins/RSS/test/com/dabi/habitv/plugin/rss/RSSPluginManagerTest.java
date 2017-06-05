package com.dabi.habitv.plugin.rss;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.plugintester.BasePluginProviderTester;

public class RSSPluginManagerTest extends BasePluginProviderTester {

	@Test
	public final void testProviderRSS() throws InstantiationException, IllegalAccessException, DownloadFailedException {
		final RSSPluginManager plugin = new RSSPluginManager() {

			@Override
			public Set<CategoryDTO> findCategory() {
				checkCategories(super.findCategory());
				return new LinkedHashSet<>(Arrays
		                .asList(buildCat()));
			}

			private CategoryDTO buildCat() {
				CategoryDTO categoryDTO = new CategoryDTO("rss", "dessinemoileco", "http://www.dailymotion.com/rss/user/Dessinemoileco/1", FrameworkConf.MP4);
				categoryDTO.setDownloadable(true);
				return categoryDTO;
			}
		};
		testPluginProvider(plugin, true);
	}

}
