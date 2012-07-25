package com.dabi.habitv.provider.m6w9;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.provider.m6w9.W9Conf;

public class W9RetreiverTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFindCategory() {
		final CategoryDTO category = new CategoryDTO("w9", "CAUCHEMAR EN CUISINE", "3374", "mp4");
		final Set<EpisodeDTO> episodes = Retriever.findEpisodeByCategory(null, category, RetrieverUtils.getInputStreamFromUrl((W9Conf.CATALOG_URL)));
		assertTrue(!episodes.isEmpty());
		final Iterator<EpisodeDTO> it = episodes.iterator();
		final EpisodeDTO episode = it.next();
		assertNotNull(episode);
		assertNotNull(episode.getName());
		assertNotNull(episode.getVideoUrl());
	}

}
