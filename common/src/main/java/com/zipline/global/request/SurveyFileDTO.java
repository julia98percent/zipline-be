package com.zipline.global.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;

@Getter
public class SurveyFileDTO {
	private String fileName;
	private MultipartFile file;
	private String uploadedUrl;

	private SurveyFileDTO(String fileName, MultipartFile file, String uploadedUrl) {
		this.fileName = fileName;
		this.file = file;
		this.uploadedUrl = uploadedUrl;
	}

	public static SurveyFileDTO createSurveyFileDTOWithoutUploadedUrl(String fileName, MultipartFile file) {
		return new SurveyFileDTO(fileName, file, null);
	}

	public static SurveyFileDTO createSurveyFileDTO(String fileName, MultipartFile file, String uploadedUrl) {
		return new SurveyFileDTO(fileName, file, uploadedUrl);
	}
}
