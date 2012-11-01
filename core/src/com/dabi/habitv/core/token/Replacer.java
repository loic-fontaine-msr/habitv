package com.dabi.habitv.core.token;

import com.dabi.habitv.framework.plugin.api.dto.EpisodeDTO;

abstract class Replacer {

	private final Replacer replacer;

	Replacer() {
		super();
		replacer = null;
	}

	Replacer(Replacer replacer) {
		super();
		this.replacer = replacer;
	}

	abstract String replace(EpisodeDTO episode);

	Replacer getReplacer() {
		return replacer;
	}
	
}
