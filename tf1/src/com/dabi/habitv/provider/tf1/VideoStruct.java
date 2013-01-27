package com.dabi.habitv.provider.tf1;

import java.util.Collection;

class VideoStruct {
	private final boolean hd;
	private final Collection<String> mediaIdList;

	public VideoStruct(final boolean hd, final Collection<String> mediaIdList) {
		super();
		this.hd = hd;
		this.mediaIdList = mediaIdList;
	}

	public boolean isHd() {
		return hd;
	}

	public Collection<String> getMediaIdList() {
		return mediaIdList;
	}

}
