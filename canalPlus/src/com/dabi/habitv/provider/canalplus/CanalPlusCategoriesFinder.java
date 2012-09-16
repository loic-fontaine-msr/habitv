package com.dabi.habitv.provider.canalplus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.dabi.habitv.framework.plugin.api.dto.CategoryDTO;
import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;
import com.dabi.habitv.provider.canalplus.initplayer.entities.INITPLAYER;
import com.dabi.habitv.provider.canalplus.initplayer.entities.SELECTION;
import com.dabi.habitv.provider.canalplus.initplayer.entities.THEMATIQUE;
import com.dabi.habitv.provider.canalplus.mea.entities.MEA;
import com.dabi.habitv.provider.canalplus.mea.entities.MEAS;

class CanalPlusCategoriesFinder {

	private final ClassLoader classLoader;

	CanalPlusCategoriesFinder(final ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	protected Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categories = new HashSet<>();

		final INITPLAYER initplayer = (INITPLAYER) RetrieverUtils.unmarshalInputStream(RetrieverUtils.getInputStreamFromUrl(CanalPlusConf.INITPLAYER_URL),
				CanalPlusConf.INITPLAYER_PACKAGE_NAME, classLoader);
		CategoryDTO categoryDTO;
		for (THEMATIQUE thematique : initplayer.getTHEMATIQUES().getTHEMATIQUE()) {
			for (SELECTION selection : thematique.getSELECTIONS().getSELECTION()) {
				categoryDTO = new CategoryDTO(CanalPlusConf.NAME, selection.getNOM(), String.valueOf(selection.getID()), CanalPlusConf.EXTENSION);
				categories.add(categoryDTO);
				categoryDTO.addSubCategories(getCategoryById(String.valueOf(selection.getID())));
			}
		}
		return categories;
	}

	private Collection<CategoryDTO> getCategoryById(final String identifier) {
		final Set<CategoryDTO> categories = new HashSet<>();

		final MEAS meas = (MEAS) RetrieverUtils.unmarshalInputStream(RetrieverUtils.getInputStreamFromUrl(CanalPlusConf.MEA_URL + identifier),
				CanalPlusConf.MEA_PACKAGE_NAME, classLoader);
		for (MEA mea : meas.getMEA()) {
			categories.add(new CategoryDTO(CanalPlusConf.NAME, mea.getRUBRIQUAGE().getRUBRIQUE(), String.valueOf(mea.getID()), CanalPlusConf.EXTENSION));
		}

		return categories;
	}
}
