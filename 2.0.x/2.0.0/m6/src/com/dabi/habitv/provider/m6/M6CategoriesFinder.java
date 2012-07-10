package com.dabi.habitv.provider.m6;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.provider.m6.entities.Categorie;
import com.dabi.habitv.provider.m6.entities.TemplateExchangeWEB;

public final class M6CategoriesFinder {

	private M6CategoriesFinder() {

	}

	public static Set<CategoryDTO> findCategory(final ClassLoader classLoader) {
		final Set<CategoryDTO> categoryList = new HashSet<>();
		final TemplateExchangeWEB templateExchangeWEB = (TemplateExchangeWEB) RetrieverUtils.unmarshalInputStream(
				RetrieverUtils.getEncryptedInputStreamFromUrl(M6Conf.CATALOG_URL, M6Conf.ENCRYPTION, M6Conf.SECRET_KEY), M6Conf.PACKAGE_NAME, classLoader);
		final List<Categorie> categories = templateExchangeWEB.getCategorie();
		for (Categorie categorie : categories) {
			final List<Object> subCategorieList = categorie.getCategorieOrNomOrLiens();
			for (Object subCategorieObject : subCategorieList) {
				if (subCategorieObject instanceof Categorie) {
					final Categorie subCategorie = (Categorie) subCategorieObject;
					final String categorieName = (String) subCategorie.getCategorieOrNomOrLiens().get(0);
					categoryList.add(new CategoryDTO(M6Conf.NAME, categorieName, String.valueOf(subCategorie.getId()), M6Conf.EXTENSION));
				}

			}
		}
		return categoryList;
	}

}
