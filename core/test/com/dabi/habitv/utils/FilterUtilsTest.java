package com.dabi.habitv.utils;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.dabi.habitv.api.plugin.dto.EpisodeDTO;

public class FilterUtilsTest {

	@Test
	public void testFilterByIncludeExcludeAndDownloaded() {
		EpisodeDTO episode = new EpisodeDTO(null, "test EP test", "1");
		List<String> includeList = Arrays.asList(".*ep.*");
		List<String> excludeList = Collections.emptyList();
		boolean result = FilterUtils.filterByIncludeExcludeAndDownloaded(episode, includeList, excludeList);
		assertTrue(result);
	}
	
	@Test
	public void testFilterByIncludeExcludeAndDownloadedFalse() {
		EpisodeDTO episode = new EpisodeDTO(null, "test EP test", "1");
		List<String> includeList = Arrays.asList(".*lol.*");
		List<String> excludeList = Collections.emptyList();
		boolean result = FilterUtils.filterByIncludeExcludeAndDownloaded(episode, includeList, excludeList);
		assertFalse(result);
	}

}
