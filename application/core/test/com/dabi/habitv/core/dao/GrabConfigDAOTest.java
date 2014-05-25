package com.dabi.habitv.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.core.dao.GrabConfigDAO.LoadModeEnum;

public class GrabConfigDAOTest {

	private GrabConfigDAO dao;

	private static final String XML_FILE = "testGrabConfig.xml";

	private static final String OLD_XML_FILE = "testOldGrabconfig.xml";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		(new File(XML_FILE)).delete();
		dao = new GrabConfigDAO(XML_FILE);
	}

	@After
	public void tearDown() throws Exception {
		// (new File(XML_FILE)).delete();
	}

	@Test
	public final void testLoadOld() throws IOException {
		File file = new File(XML_FILE);
		Files.copy(new File(OLD_XML_FILE).toPath(), file.toPath());
		final Map<String, CategoryDTO> channel2CategoriesTotest = dao
				.load(LoadModeEnum.ALL);
		assertNotNull(channel2CategoriesTotest);
	}

	@Test
	public final void testSaveAndLoadGrabConfig() {
		final Map<String, CategoryDTO> channel2Categories = buildChannelMap(
				true, false);
		assertFalse(dao.exist());
		dao.saveGrabConfig(channel2Categories);
		assertTrue((new File(XML_FILE)).exists());
		final Map<String, CategoryDTO> channel2CategoriesTotest = dao
				.load(LoadModeEnum.ALL);
		assertEquals(channel2Categories, channel2CategoriesTotest);
	}

	@Test
	public final void testUpdateGrabConfig() {
		Map<String, CategoryDTO> channel2Categories = buildChannelMap(true,
				false);
		assertFalse(dao.exist());
		dao.saveGrabConfig(channel2Categories);
		assertTrue((new File(XML_FILE)).exists());
		channel2Categories = buildChannelMap(false, true);
		dao.updateGrabConfig(channel2Categories);
		final Map<String, CategoryDTO> channel2CategoriesTotest = dao
				.load(LoadModeEnum.ALL);
		final CategoryDTO category = channel2CategoriesTotest.get("channel1")
				.getSubCategories().iterator().next();
		assertEquals(category.getExclude().iterator().next(), "exc1");
		assertEquals(category.getInclude().iterator().next(), "inc1");
		assertTrue(category.getSubCategories().iterator().next().getName().equals("sub"));
	}

	private Map<String, CategoryDTO> buildChannelMap(final boolean inc,
			final boolean sup) {
		final Map<String, CategoryDTO> channel2Categories = new HashMap<>();
		Set<CategoryDTO> categories = new HashSet<>();
		List<String> includeList = null;
		List<String> excludeList = null;
		if (inc) {
			includeList = Arrays.asList(new String[] { "inc1", "inc2" });
			excludeList = Arrays.asList(new String[] { "exc1", "exc2" });
		}
		CategoryDTO category = new CategoryDTO("channel1", "cat1", "cat1I",
				includeList, excludeList, "ext");
		category.addSubCategory(new CategoryDTO("sub", "sub", "sub", "sub"));
		categories.add(category);
		category = new CategoryDTO("channel1", "cat2", "cat2I", includeList,
				excludeList, "ext2");
		if (sup) {
			category.addSubCategory(new CategoryDTO("sub2", "sub2", "sub2",
					"sub2"));
		}
		categories.add(category);
		channel2Categories.put("channel1", new CategoryDTO("channel1",
				categories));
		categories = new HashSet<>();
		category = new CategoryDTO("channel2", "cat3", "cat3I", includeList,
				excludeList, "ext");
		categories.add(category);
		channel2Categories.put("channel2", new CategoryDTO("channel2",
				categories));
		return channel2Categories;
	}
}
