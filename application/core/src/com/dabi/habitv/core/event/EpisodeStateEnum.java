package com.dabi.habitv.core.event;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum EpisodeStateEnum {
	BUILD_INDEX, DOWNLOAD_STARTING, EXPORT_STARTING, DOWNLOADED, READY, DOWNLOAD_FAILED, EXPORT_FAILED, FAILED, TO_DOWNLOAD, TO_EXPORT;

	public static final Set<EpisodeStateEnum> IN_PROGRESS = new HashSet<>(
			Arrays.asList(DOWNLOAD_STARTING, EXPORT_STARTING, DOWNLOADED, TO_DOWNLOAD,
					TO_EXPORT));

	public static final Set<EpisodeStateEnum> HAS_FAILED = new HashSet<>(
			Arrays.asList(DOWNLOAD_FAILED, EXPORT_FAILED, FAILED));
	
	public boolean isInProgress(){
		return IN_PROGRESS.contains(this);
	}
	
	public boolean hasFailed(){
		return HAS_FAILED.contains(this);
	}
}
