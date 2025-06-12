package com.zipline.global.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.zipline.global.exception.common.FileUploadException;
import com.zipline.global.exception.common.errorcode.CommonErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileValidator {

	private static final Tika tika = new Tika();
	private final Set<String> ALLOWED_MIME_TYPES;

	public FileValidator(@Value("${file.allowed-mime-types}") List<String> allowedMimeTypes) {
		this.ALLOWED_MIME_TYPES = new HashSet<>(allowedMimeTypes);
	}

	public void validateMimeType(MultipartFile file) {
		try {
			String detect = tika.detect(file.getInputStream());
			if (!ALLOWED_MIME_TYPES.contains(detect)) {
				throw new FileUploadException(CommonErrorCode.FILE_TYPE_NOT_SUPPORTED);
			}
		} catch (IOException e) {
			throw new FileUploadException(CommonErrorCode.FILE_VALIDATION_FAILED);
		}
	}

	public void validateSurveyFile(MultipartFile file) {
		validateMimeType(file);
	}

	public void validateContractFile(MultipartFile file) {
		validateMimeType(file);
	}
}
