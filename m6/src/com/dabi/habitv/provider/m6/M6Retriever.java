package com.dabi.habitv.provider.m6;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.provider.m6.entities.Categorie;
import com.dabi.habitv.provider.m6.entities.Produit;
import com.dabi.habitv.provider.m6.entities.TemplateExchangeWEB;

public final class M6Retriever {

	private M6Retriever() {

	}

	public static Set<EpisodeDTO> findEpisodeByCategory(final ClassLoader classLoader, final CategoryDTO category) {
		final TemplateExchangeWEB templateExchangeWEB = (TemplateExchangeWEB) RetrieverUtils.unmarshalInputStream(
				RetrieverUtils.getEncryptedInputStreamFromUrl(M6Conf.CATALOG_URL, M6Conf.ENCRYPTION, M6Conf.SECRET_KEY), M6Conf.PACKAGE_NAME, classLoader);

		final List<Categorie> categories = templateExchangeWEB.getCategorie();
		final Set<EpisodeDTO> episodeList = new HashSet<>();
		for (Categorie categorie : categories) {
			final List<Object> subCategorieList = categorie.getCategorieOrNomOrLiens();
			for (Object subCategorieObject : subCategorieList) {
				if (subCategorieObject instanceof Categorie) {
					final Categorie subCategorie = (Categorie) subCategorieObject;
					final String categorieName = (String) subCategorie.getCategorieOrNomOrLiens().get(0);
					if (categorieName.equals(category.getName()) || subCategorie.getId().toString().equals(category.getId())) {
						for (Produit produit : subCategorie.getProduit()) {
							episodeList.add(new EpisodeDTO(category, produit.getNom(), produit.getFichemedia().getVideoUrl()));
						}
					}
				}

			}
		}
		return episodeList;
	}

}
