package com.dabi.habitv.provider.m6w9;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.provider.m6.entities.Categorie;
import com.dabi.habitv.provider.m6.entities.Produit;
import com.dabi.habitv.provider.m6.entities.TemplateExchangeWEB;

public final class Retriever {

	private Retriever() {

	}

	public static Set<EpisodeDTO> findEpisodeByCategory(final ClassLoader classLoader, final CategoryDTO category, final InputStream inputStream) {
		final TemplateExchangeWEB templateExchangeWEB = (TemplateExchangeWEB) RetrieverUtils.unmarshalInputStream(inputStream, M6W9Conf.PACKAGE_NAME,
				classLoader);

		final List<Categorie> categories = templateExchangeWEB.getCategorie();
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		for (final Categorie categorie : categories) {
			final List<Object> subCategorieList = categorie.getCategorieOrNomOrLiens();
			for (final Object subCategorieObject : subCategorieList) {
				if (subCategorieObject instanceof Categorie) {
					final Categorie subCategorie = (Categorie) subCategorieObject;
					final String categorieName = (String) subCategorie.getCategorieOrNomOrLiens().get(0);
					if (categorieName.equals(category.getName()) || subCategorie.getId().toString().equals(category.getId())) {
						for (final Produit produit : subCategorie.getProduit()) {
							episodeList.add(new EpisodeDTO(category, produit.getNom(), produit.getFichemedia().getVideoUrl()));
						}
					}
				}

			}
		}
		return episodeList;
	}
}
