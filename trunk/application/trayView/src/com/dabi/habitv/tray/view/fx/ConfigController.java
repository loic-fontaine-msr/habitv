package com.dabi.habitv.tray.view.fx;

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

		nbrMaxAttempts.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				System.out.println(((TextField) event.getSource()).getText());
			}
		});
	}
}
