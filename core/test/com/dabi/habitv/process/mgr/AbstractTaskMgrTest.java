package com.dabi.habitv.process.mgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dabi.habitv.framework.plugin.exception.TechnicalException;
import com.dabi.habitv.process.task.AbstractTask;

public class AbstractTaskMgrTest {

	private TaskMgr<AbstractTask<Object>, Object> taskMgr;

	private int test1 = 0;

	private int test2 = 0;

	private boolean allTreatmentDone;

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

	private void buildSimultaneousTask(final int taskNb, final String cat, final String cat2, final boolean shutdown) {
		taskMgr = new TaskMgr<AbstractTask<Object>, Object>(taskNb, new TaskMgrListener() {

			@Override
			public void onAllTreatmentDone() {
				allTreatmentDone = true;
			}

			@Override
			public void onFailed() {
				allTreatmentDone = false;
			}
		}) {
		};
		AbstractTask<Object> task = new AbstractTaskForTest() {

			@Override
			protected Object doCall() {
				test2 = 0;
				try {
					Thread.sleep(500);
				} catch (final InterruptedException e) {
					fail();
				}
				if (test2 == 0) {
					test1++;
				} else {
					test1 = -1;
				}
				return null;
			}

		};
		if (cat == null) {
			taskMgr.addTask(task);
		} else {
			taskMgr.addTask(task, cat);
		}
		task = new AbstractTaskForTest() {

			@Override
			protected Object doCall() {
				try {
					Thread.sleep(100);
				} catch (final InterruptedException e) {
					fail();
				}
				test2++;
				return null;
			}

		};
		if (cat2 == null) {
			taskMgr.addTask(task);
		} else {
			taskMgr.addTask(task, cat2);
		}
		if (shutdown) {
			taskMgr.shutdown(1000);
		}
	}

	@Test
	public final void canRunSimultaneusAsyncTask() {
		buildSimultaneousTask(2, null, null, true);
		assertEquals(1, test2);
		assertEquals(-1, test1);
	}

	@Test
	public final void canRunOnly1TaskSimulta() {
		buildSimultaneousTask(1, null, null, true);
		assertEquals(1, test2);
		assertEquals(1, test1);
	}

	@Test
	public final void canRunSimultaneusAsyncTaskOnDifferentThreadPoolExecutor() {
		// 2 differents category for 2 PoolExecutor
		buildSimultaneousTask(1, "1", "2", true);
		assertEquals(1, test2);
		assertEquals(-1, test1);
	}

	private void buildSimultaneousTaskWithError(final int taskNb, final String cat, final String cat2) {
		taskMgr = new TaskMgr<AbstractTask<Object>, Object>(taskNb, new TaskMgrListener() {

			@Override
			public void onAllTreatmentDone() {
				allTreatmentDone = true;
			}

			@Override
			public void onFailed() {
				allTreatmentDone = false;
			}

		}) {
		};
		AbstractTask<Object> task = new AbstractTaskForTest() {

			@Override
			protected Object doCall() {
				throw new TechnicalException("error");
			}

		};
		if (cat == null) {
			taskMgr.addTask(task);
		} else {
			taskMgr.addTask(task, cat);
		}
		task = new AbstractTaskForTest() {

			@Override
			protected Object doCall() {
				try {
					Thread.sleep(100);
				} catch (final InterruptedException e) {
					fail();
				}
				test2++;
				return null;
			}

		};
		if (cat2 == null) {
			taskMgr.addTask(task);
		} else {
			taskMgr.addTask(task, cat2);
		}
		taskMgr.shutdown(1000);
	}

	@Test
	public final void stopOnTechnicalError() {
		buildSimultaneousTaskWithError(2, null, null);
		assertEquals(0, test2);
		assertEquals(0, test1);
	}

	@Test
	public final void indicateWhenAllTreatmentAreDone() {
		buildSimultaneousTask(2, null, null, false);
		assertFalse(allTreatmentDone);
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
			fail();
		}
		assertTrue(allTreatmentDone);
		taskMgr.shutdown(0);
	}
}
