package com.dabi.habitv.tray.controller;

import com.dabi.habitv.core.config.UserConfig;

import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
		addTooltips();
	}

	private void addTooltips() {
		downloadOuput
				.setTooltip(new Tooltip(
						"Modèle de stockage des téléchargements, vous pouvez utiliser les tokens suivant : \n"
								+ "#EPISODE# : nom de l'épisode\n"
								+ "#CHANNEL# : nom du fournisseur\n"
								+ "#CATEGORY# : nom de la catégorie\n"
								+ "#EXTENSION# : extension du fichier\n"
								+ "#NUM# : le numéro d'épisode pour le fournisseur\n"
								+ "#DATE§yyyyMMdd# : la date de téléchargement de l'épisode, le paramètre après § peut être modifié suivant : Format de date"));
		
		nbrMaxAttempts.setTooltip(new Tooltip("Nombre de tentatives de téléchargement d'un épisode avant d'arrêter de retenter."));
		daemonCheckTimeSec.setTooltip(new Tooltip("Période de temps entre 2 recherches automatiques de téléchargement."));
		autoUpdate.setTooltip(new Tooltip("si coché habiTv se mettra à jour automatiquement."));
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
						UserConfig userConfig = getController()
								.loadUserConfig();
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
						UserConfig userConfig = getController()
								.loadUserConfig();
						userConfig.setMaxAttempts(Integer
								.parseInt(nbrMaxAttempts.getText()));
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
						UserConfig userConfig = getController()
								.loadUserConfig();
						userConfig.setDemonCheckTime(Integer
								.parseInt(daemonCheckTimeSec.getText()));
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
						UserConfig userConfig = getController()
								.loadUserConfig();
						userConfig.setUpdateOnStartup(autoUpdate.isSelected());
						getController().saveConfig(userConfig);
					}
				});
			}
		});
	}
}
