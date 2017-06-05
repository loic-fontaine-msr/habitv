package com.dabi.habitv.plugin.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.FrameworkConf;
import com.dabi.habitv.plugintester.BasePluginProviderTester;

public class FilePluginManagerTest extends BasePluginProviderTester {

	private static final String TEST_FILE = "target/test.txt";

	@Test
	public final void testProviderFile() throws InstantiationException, IllegalAccessException, DownloadFailedException, IOException {
		File dest = new File(TEST_FILE);
		dest.delete();
		try (FileOutputStream os = new FileOutputStream(dest)){
			os.write("test".getBytes());
		}
		File done = new File(TEST_FILE + ".done");
		done.renameTo(dest);
		final FilePluginManager plugin = new FilePluginManager() {

			@Override
			public Set<CategoryDTO> findCategory() {
				checkCategories(super.findCategory());
				return new LinkedHashSet<>(Arrays.asList(buildCat()));
			}

			private CategoryDTO buildCat() {
				CategoryDTO categoryDTO = new CategoryDTO("file", TEST_FILE, TEST_FILE, FrameworkConf.MP4);
				categoryDTO.setDownloadable(true);
				return categoryDTO;
			}
		};
		testPluginProvider(plugin, true);
	}

}
