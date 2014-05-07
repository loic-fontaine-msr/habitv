package com.dabi.habitv.api.plugin.dto;

import java.util.HashMap;
import java.util.Map;

public class DownloadParamDTO {
	private final String downloadInput;

	private final String downloadOutput;

	private final String extension;

	private final Map<String, String> params = new HashMap<>();

	public DownloadParamDTO(final String downloadInput,
			final String downloadOutput, final String extension) {
		super();
		this.downloadInput = downloadInput;
		this.downloadOutput = downloadOutput;
		this.extension = extension;
	}

	public String getDownloadInput() {
		return downloadInput;
	}

	public String getDownloadOutput() {
		return downloadOutput;
	}

	public String getExtension() {
		return extension;
	}

	public String getParam(final String key) {
		return params.get(key);
	}

	public void addParam(final String key, final String value) {
		params.put(key, value);
	}

	public Map<String, String> getParams() {
		return params;
	}

	public static DownloadParamDTO buildDownloadParam(
			final DownloadParamDTO downloadParam, final String downloadInput) {
		final DownloadParamDTO downloadParamDTO = new DownloadParamDTO(
				downloadInput, downloadParam.getDownloadOutput(),
				downloadParam.getExtension());
		downloadParamDTO.getParams().putAll(downloadParam.getParams());
		return downloadParamDTO;
	}

	@Override
	public String toString() {
		return "DownloadParamDTO [downloadInput=" + downloadInput
				+ ", downloadOutput=" + downloadOutput + ", extension="
				+ extension + ", params=" + params + "]";
	}
}
