package com.dabi.habitv.provider.tvSubtitles;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.exception.TechnicalException;

public final class TvSubtitlesCategoriesFinder {

	private TvSubtitlesCategoriesFinder() {

	}

	public static Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categoryList = new HashSet<>();
		try {
			Source source = new Source(new URL(TvSubtitlesConf.CATEGORIES_URL));
			for (Segment segment : source.getAllStartTags("td align=left style=\"padding: 0 4px;\"")) {
				List<Element> categorieSegment = segment.getChildElements().get(0).getChildElements();
				String name = categorieSegment.get(0).getChildElements().get(0).getContent().toString();
				String identifier = categorieSegment.get(0).getAttributeValue("href");
				categoryList.add(new CategoryDTO(name, identifier));
			}
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
		return categoryList;
	}

}
