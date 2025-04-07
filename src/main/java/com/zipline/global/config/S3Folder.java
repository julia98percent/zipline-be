package com.zipline.global.config;

public enum S3Folder {
	SURVEYS("surveys/");

	private final String folderPrefix;

	S3Folder(String folderPrefix) {
		this.folderPrefix = folderPrefix;
	}

	public String getFolderPrefix() {
		return folderPrefix;
	}
}
