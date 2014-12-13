package com.dabi.habitv.core.token;

import java.util.List;

import com.dabi.habitv.api.plugin.dto.EpisodeDTO;

abstract class Replacer {

	private final Replacer replacer;

	Replacer() {
		super();
		replacer = null;
	}

	Replacer(final Replacer replacer) {
		super();
		this.replacer = replacer;
	}

	abstract String replace(EpisodeDTO episode, List<String> params);

	Replacer getReplacer() {
		return replacer;
	}

}
