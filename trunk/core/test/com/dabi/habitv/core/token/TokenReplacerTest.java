package com.dabi.habitv.core.token;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

public class TokenReplacerTest {

	private EpisodeDTO episode;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		final CategoryDTO category = new CategoryDTO("channel", "catName", "catId", "extension");
		episode = new EpisodeDTO(category, "epName", "videoUrl");
		episode.setNum(5);
		TokenReplacer.setCutSize(5);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testReplaceAll() {
		assertEquals(
				(new SimpleDateFormat("yyyyMM").format(new Date()) + " epN channel catName extension " + new SimpleDateFormat("yyyy").format(new Date())),
				TokenReplacer.replaceAll("#DATETIME§yyyyMM# #EPISODE§3# #CHANNEL# #CATEGORY# #EXTENSION# #DATETIME§yyyy#", episode));
		assertEquals("channel epName channel catName extension", TokenReplacer.replaceAll("#CHANNEL# #EPISODE# #CHANNEL# #CATEGORY# #EXTENSION#", episode));
	}

}
