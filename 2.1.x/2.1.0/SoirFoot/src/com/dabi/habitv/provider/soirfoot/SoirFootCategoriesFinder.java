package com.dabi.habitv.provider.soirfoot;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;

public final class SoirFootCategoriesFinder {

	private SoirFootCategoriesFinder() {

	}

	public static Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categoryList = new HashSet<>();
		try {
			Source source = new Source(new URL(SoirFootConf.HOME_URL));
			for (Segment segment : source.getAllStartTags("ul class=\"hidden_li\"")) {
				for (Segment segmentLi : segment.getChildElements().get(0).getChildElements()) {
					Element segmentA = segmentLi.getChildElements().get(0);
					String[] urlTab = segmentA.getAttributeValue("href").split("/");
					String identifier = urlTab[urlTab.length - 1];
					String name = segmentA.getContent().toString();
					categoryList.add(new CategoryDTO(SoirFootConf.NAME, name, identifier, SoirFootConf.EXTENSION));
				}
			}
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
		return categoryList;
	}

}
