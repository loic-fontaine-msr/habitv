package com.dabi.habitv.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
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
		(new File(XML_FILE)).delete();
	}

	@Test
	public final void testSaveAndLoadGrabConfig() {
		final Map<String, Set<CategoryDTO>> channel2Categories = buildChannelMap(true, false);
		assertFalse(dao.exist());
		dao.saveGrabConfig(channel2Categories);
		assertTrue((new File(XML_FILE)).exists());
		final Map<String, Set<CategoryDTO>> channel2CategoriesTotest = dao.load(LoadModeEnum.ALL);
		assertEquals(channel2Categories, channel2CategoriesTotest);
	}

	@Test
	public final void testUpdateGrabConfig() {
		Map<String, Set<CategoryDTO>> channel2Categories = buildChannelMap(true, false);
		assertFalse(dao.exist());
		dao.saveGrabConfig(channel2Categories);
		assertTrue((new File(XML_FILE)).exists());
		channel2Categories = buildChannelMap(false, true);
		dao.updateGrabConfig(channel2Categories);
		final Map<String, Set<CategoryDTO>> channel2CategoriesTotest = dao.load(LoadModeEnum.ALL);
		assertEquals(channel2CategoriesTotest.get("channel1").toArray(new CategoryDTO[0])[0].getExclude().toArray(new String[0])[0], "exc1");
		assertEquals(channel2CategoriesTotest.get("channel1").toArray(new CategoryDTO[0])[0].getInclude().toArray(new String[0])[0], "inc1");
		assertTrue(channel2CategoriesTotest.get("channel1").toArray(new CategoryDTO[0])[0].getSubCategories().get(0).getName().equals("sub2"));
	}

	private Map<String, Set<CategoryDTO>> buildChannelMap(final boolean inc, final boolean sup) {
		final Map<String, Set<CategoryDTO>> channel2Categories = new HashMap<>();
		Set<CategoryDTO> categories = new HashSet<>();
		List<String> includeList = null;
		List<String> excludeList = null;
		if (inc) {
			includeList = Arrays.asList(new String[] { "inc1", "inc2" });
			excludeList = Arrays.asList(new String[] { "exc1", "exc2" });
		}
		CategoryDTO category = new CategoryDTO("channel1", "cat1", "cat1I", includeList, excludeList, "ext");
		categories.add(category);
		category = new CategoryDTO("channel1", "cat2", "cat2I", includeList, excludeList, "ext2");
		category.addSubCategory(new CategoryDTO("sub", "sub", "sub", "sub"));
		if (sup) {
			category.addSubCategory(new CategoryDTO("sub2", "sub2", "sub2", "sub2"));
		}
		categories.add(category);
		channel2Categories.put("channel1", categories);
		categories = new HashSet<>();
		category = new CategoryDTO("channel2", "cat3", "cat3I", includeList, excludeList, "ext");
		categories.add(category);
		channel2Categories.put("channel2", categories);
		return channel2Categories;
	}
}
