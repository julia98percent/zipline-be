package com.zipline.global.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.zipline.global.exception.custom.FileUploadException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileValidator {

	private static final Tika tika = new Tika();
	private final Set<String> ALLOWED_MIME_TYPES;

	public FileValidator(@Value("${file.allowed-mime-types}") List<String> allowedMimeTypes) {
		this.ALLOWED_MIME_TYPES = new HashSet<>(allowedMimeTypes);
	}

	public void validateSurveyFile(MultipartFile file) {
		try {
			String detect = tika.detect(file.getInputStream());
			if (!ALLOWED_MIME_TYPES.contains(detect)) {
				throw new FileUploadException("지원하지 않는 MIME TYPE 입니다.", HttpStatus.BAD_REQUEST);
			}
		} catch (IOException e) {
			throw new FileUploadException("파일 MIME 타입 검증에 실패하였습니다.", HttpStatus.BAD_REQUEST);
		}
	}
}
