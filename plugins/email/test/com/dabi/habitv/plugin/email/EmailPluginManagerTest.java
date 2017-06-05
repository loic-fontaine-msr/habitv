package com.dabi.habitv.plugin.email;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.exception.DownloadFailedException;
import com.dabi.habitv.framework.plugin.tpl.TemplateUtils;
import com.dabi.habitv.plugintester.BasePluginProviderTester;
import com.google.common.collect.ImmutableMap;

public class EmailPluginManagerTest extends BasePluginProviderTester {

	@Test
	public final void test() throws InstantiationException, IllegalAccessException, DownloadFailedException {
		final EmailPluginManager plugin = new EmailPluginManager() {

			@Override
			public Set<CategoryDTO> findCategory() {
				checkCategories(super.findCategory());
				return new LinkedHashSet<>(Arrays.asList(buildCat()));
			}

			private CategoryDTO buildCat() {
				CategoryDTO sampleCat = TemplateUtils.buildSampleCat(getName(), "testhabitv",
		                ImmutableMap.of(EmailConf.HOST, "IMAP.gmail.com", EmailConf.USER, "testhabitv", EmailConf.PASSWORD, "HabiTV410"));
				CategoryDTO father = new CategoryDTO(getName(), "IMAP", "IMAP", "ext");
				father.addSubCategory(sampleCat);
				return sampleCat;
			}
		};
		testPluginProvider(plugin, true);
	}

}
