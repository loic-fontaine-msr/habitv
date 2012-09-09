import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestManifestReader {

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
	public final void test() throws FileNotFoundException, IOException {
		final JarInputStream jarStream = new JarInputStream(new FileInputStream("test/framework.jar"));
		final Manifest mf = jarStream.getManifest();
		assertEquals("2.0", mf.getMainAttributes().get(Attributes.Name.IMPLEMENTATION_VERSION));
	}

}
