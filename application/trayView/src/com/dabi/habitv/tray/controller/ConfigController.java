package com.dabi.habitv.tray.controller;

import com.dabi.habitv.core.config.UserConfig;

import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class ConfigController extends BaseController {

	private TextField downloadOuput;

	private TextField nbrMaxAttempts;

	private TextField daemonCheckTimeSec;

	private CheckBox autoUpdate;

	public ConfigController(TextField downloadOuput, TextField nbrMaxAttempts,
			TextField daemonCheckTimeSec, CheckBox autoUpdate) {
		super();
		this.downloadOuput = downloadOuput;
		this.nbrMaxAttempts = nbrMaxAttempts;
		this.daemonCheckTimeSec = daemonCheckTimeSec;
		this.autoUpdate = autoUpdate;
	}

	public void init() {
		loadConfig();
		addButtonActions();
	}

	private void loadConfig() {
		UserConfig userConfig = getController().loadUserConfig();
		downloadOuput.setText(userConfig.getDownloadOuput());
		nbrMaxAttempts.setText(String.valueOf(userConfig.getMaxAttempts()));
		daemonCheckTimeSec.setText(String.valueOf(userConfig
				.getDemonCheckTime()));
		autoUpdate.setSelected(userConfig.updateOnStartup());
	}

	private void addButtonActions() {
		downloadOuput.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				planTaskIfNot(new Runnable() {
					
					@Override
					public void run() {
						UserConfig userConfig = getController().loadUserConfig();
						userConfig.setDownloadOuput(downloadOuput.getText());
						getController().saveConfig(userConfig);
					}
				});
			}
		});
		
		nbrMaxAttempts.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				planTaskIfNot(new Runnable() {
					
					@Override
					public void run() {
						UserConfig userConfig = getController().loadUserConfig();
						userConfig.setMaxAttempts(Integer.parseInt(nbrMaxAttempts.getText()));
						getController().saveConfig(userConfig);
					}
				});
			}
		});
		
		daemonCheckTimeSec.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				planTaskIfNot(new Runnable() {
					
					@Override
					public void run() {
						UserConfig userConfig = getController().loadUserConfig();
						userConfig.setDemonCheckTime(Integer.parseInt(daemonCheckTimeSec.getText()));
						getController().saveConfig(userConfig);
					}
				});
			}
		});
		
		autoUpdate.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				planTaskIfNot(new Runnable() {
					
					@Override
					public void run() {
						UserConfig userConfig = getController().loadUserConfig();
						userConfig.setUpdateOnStartup(autoUpdate.isSelected());
						getController().saveConfig(userConfig);
					}
				});
			}
		});
	}
}
