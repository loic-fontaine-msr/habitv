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
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public class W9CategoriesFinderTest {

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
		final Set<CategoryDTO> categories = CategoriesFinder.findCategory(null, RetrieverUtils.getInputStreamFromUrl(W9Conf.CATALOG_URL), W9Conf.NAME);
		assertTrue(!categories.isEmpty());
		final Iterator<CategoryDTO> it = categories.iterator();
		final CategoryDTO category = it.next();
		assertNotNull(category);
		assertNotNull(category.getChannel());
		assertNotNull(category.getName());
	}

}
