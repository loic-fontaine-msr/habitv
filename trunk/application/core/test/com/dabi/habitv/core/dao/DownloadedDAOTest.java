/**
 * 
 */
package com.dabi.habitv.core.dao;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author bidou
 * 
 */
public class DownloadedDAOTest {

	private DownloadedDAO dao;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		initDAO();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	private void initDAO() {
		dao = new DownloadedDAO("channel", "tvshow", ".");
	}

	@Test
	public final void canAddDownloadedFilesAnReadIt() {
		final String[] toAdd = new String[] { "test1", "test2" };
		dao.initIndex();
		initDAO();
		assertTrue(!dao.isIndexCreated());
		dao.addDownloadedFiles(toAdd);
		initDAO();
		assertTrue(dao.isIndexCreated());
		final Set<String> toTest = dao.findDownloadedFiles();
		assertArrayEquals(toAdd, toTest.toArray());
	}

	@Test
	public final void findDownloadedFilesReturnEmptyIfNoIndex() {
		dao.initIndex();
		final Set<String> toTest = dao.findDownloadedFiles();
		assertTrue(toTest.isEmpty());
	}

}
