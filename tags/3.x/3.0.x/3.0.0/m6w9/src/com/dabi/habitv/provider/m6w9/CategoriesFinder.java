package com.dabi.habitv.provider.m6w9;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.provider.m6.entities.Categorie;
import com.dabi.habitv.provider.m6.entities.TemplateExchangeWEB;

public final class CategoriesFinder {

	private CategoriesFinder() {

	}

	public static Set<CategoryDTO> findCategory(final ClassLoader classLoader, final InputStream inputStream, final String channel) {
		final Set<CategoryDTO> categoryList = new HashSet<>();
		final TemplateExchangeWEB templateExchangeWEB = (TemplateExchangeWEB) RetrieverUtils.unmarshalInputStream(inputStream, M6W9Conf.PACKAGE_NAME,
				classLoader);
		final List<Categorie> categories = templateExchangeWEB.getCategorie();
		for (final Categorie categorie : categories) {
			final List<Object> subCategorieList = categorie.getCategorieOrNomOrLiens();
			for (final Object subCategorieObject : subCategorieList) {
				if (subCategorieObject instanceof Categorie) {
					final Categorie subCategorie = (Categorie) subCategorieObject;
					final String categorieName = (String) subCategorie.getCategorieOrNomOrLiens().get(0);
					categoryList.add(new CategoryDTO(channel, categorieName, String.valueOf(subCategorie.getId()), M6W9Conf.EXTENSION));
				}

			}
		}
		return categoryList;
	}

}
