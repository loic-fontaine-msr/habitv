package com.dabi.habitv.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;

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
		final Map<String, Set<CategoryDTO>> channel2Categories = new HashMap<>();
		Set<CategoryDTO> categories = new HashSet<>();
		CategoryDTO category = new CategoryDTO("channel1", "cat1", "cat1I", Arrays.asList(new String[] { "inc1", "inc2" }), Arrays.asList(new String[] {
				"exc1", "exc2" }), "ext");
		categories.add(category);
		category = new CategoryDTO("channel1", "cat2", "cat2I", Arrays.asList(new String[] { "inc1", "inc2" }), Arrays.asList(new String[] { "exc1", "exc2" }),
				"ext2");
		category.addSubCategory(new CategoryDTO("sub", "sub", "sub", "sub"));
		categories.add(category);
		channel2Categories.put("channel1", categories);
		categories = new HashSet<>();
		category = new CategoryDTO("channel2", "cat3", "cat3I", Arrays.asList(new String[] { "inc1", "inc2" }), Arrays.asList(new String[] { "exc1", "exc2" }),
				"ext");
		categories.add(category);
		channel2Categories.put("channel2", categories);
		assertFalse(dao.exist());
		dao.saveGrabConfig(channel2Categories);
		assertTrue((new File(XML_FILE)).exists());
		final Map<String, Set<CategoryDTO>> channel2CategoriesTotest = dao.load();
		assertEquals(channel2Categories, channel2CategoriesTotest);
	}
}
