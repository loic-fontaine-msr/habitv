package com.dabi.habitv.launcher.tray;

import java.util.EventListener;

public interface HabiTvListener extends EventListener {
	void processChanged(ProcessChangedEvent event);
	void episodeChanged(EpisodeChangedEvent event);
}
