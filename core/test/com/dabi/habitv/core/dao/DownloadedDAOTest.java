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

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;

/**
 * @author bidou
 * 
 */
public class DownloadedDAOTest {

	private DownloadedDAO dao;
	private CategoryDTO category;

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
		category = new CategoryDTO("channel", "tvshow", "channel", "mp4");
		dao = new DownloadedDAO(category, ".");
	}

	@Test
	public final void canAddDownloadedFilesAnReadIt() {
		final EpisodeDTO[] toAdd = new EpisodeDTO[] {
				new EpisodeDTO(category, "test1", "test1"),
				new EpisodeDTO(category, "test2", "test2") };
		dao.initIndex();
		initDAO();
		assertTrue(!dao.isIndexCreated());
		dao.addDownloadedFiles(false, toAdd);
		initDAO();
		assertTrue(dao.isIndexCreated());
		final Set<String> toTest = dao.findDownloadedFiles();
		assertArrayEquals(
				new String[] { toAdd[0].getName(), toAdd[1].getName() },
				toTest.toArray());
	}

	@Test
	public final void findDownloadedFilesReturnEmptyIfNoIndex() {
		dao.initIndex();
		final Set<String> toTest = dao.findDownloadedFiles();
		assertTrue(toTest.isEmpty());
	}

}
