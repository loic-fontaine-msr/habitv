package com.dabi.habitv.tray.utils;

import com.dabi.habitv.tray.model.ActionProgress;

public class LabelUtils {
	public static String buildStateLabel(ActionProgress actionProgress) {
		StringBuilder stateLabel = new StringBuilder();
		switch (actionProgress.getState()) {
		case DOWNLOAD_FAILED:
			stateLabel.append("Echoué : ");
			break;
		case DOWNLOADED:
			stateLabel.append("Téléchargé");
			break;
		case DOWNLOADING:
			stateLabel.append("");
			break;
		case EXPORT_FAILED:
			stateLabel.append("Export échoué : ");
			break;
		case EXPORTING:
			stateLabel.append("Export : ");
			break;
		case TO_DOWNLOAD:
			stateLabel.append("A télécharger");
			break;
		case FAILED:
			stateLabel.append("Echoué : ");
			break;
		case READY:
			stateLabel.append("Terminé");
			break;
		case TO_EXPORT:
			stateLabel.append("Prêt pour l'export");
			break;
		default:
			break;
		}
		if (actionProgress.getInfo() != null
				&& !actionProgress.getInfo().isEmpty()) {
			stateLabel.append(actionProgress.getInfo());
		}
		return stateLabel.toString();
	}
}
