package com.dabi.habitv.packaging;

import com.dabi.habitv.tray.HabiTvSplashScreen;
import com.dabi.habitv.utils.LogUtils;

import javafx.application.Application;
import javafx.stage.Stage;

public class HabiTvFxRunner extends Application {

	private HabiTvSplashScreen habiTvSplashScreen;
	
	public HabiTvFxRunner() {
		this.habiTvSplashScreen = new HabiTvSplashScreen();
	}

	@Override
	public void start(final Stage initStage) throws Exception {
		habiTvSplashScreen.start(initStage);		
	}
	
	public static void main(String[] args) {
		LogUtils.updateLog4jConfiguration();
		launch(args);
	}	

}