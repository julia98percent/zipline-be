package com.zipline.global.util;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.zipline.global.config.S3Folder;
import com.zipline.global.exception.custom.FileUploadException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class S3FileUploader {

	@Value("${aws.s3.bucket}")
	private String bucket;

	private final AmazonS3 amazonS3;
	private final FileValidator fileValidator;

	public Map<Long, String> uploadSurveyFiles(Map<Long, MultipartFile> questionUidFileMap, S3Folder s3Folder) {
		Map<Long, String> fileUrlMap = new HashMap<>();

		for (Long questionUid : questionUidFileMap.keySet()) {
			MultipartFile file = questionUidFileMap.get(questionUid);
			fileValidator.validateSurveyFile(file);
			String uploadedUrl = uploadFile(file, s3Folder.getFolderPrefix());
			fileUrlMap.put(questionUid, uploadedUrl);
		}
		return fileUrlMap;
	}

	public List<String> uploadContractFiles(List<MultipartFile> files, S3Folder s3Folder) {
		List<String> uploadedUrls = new ArrayList<>();

		for (MultipartFile file : files) {
			fileValidator.validateContractFile(file);
			String uploadedUrl = uploadFile(file, s3Folder.getFolderPrefix());
			uploadedUrls.add(uploadedUrl);
		}

		return uploadedUrls;
	}

	public String uploadFile(MultipartFile file, String folderPrefix) {
		String originalFileName = file.getOriginalFilename();
		String storeFileName = createStoreFileName(originalFileName, folderPrefix);

		ObjectMetadata metadata = createObjectMetadata(file);

		try (InputStream inputStream = file.getInputStream()) {
			amazonS3.putObject(new PutObjectRequest(bucket, storeFileName, inputStream, metadata));
		} catch (IOException e) {
			throw new FileUploadException("파일 업로드 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return amazonS3.getUrl(bucket, storeFileName).toString();
	}

	private ObjectMetadata createObjectMetadata(MultipartFile multipartFile) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(multipartFile.getContentType());
		objectMetadata.setContentLength(multipartFile.getSize());
		return objectMetadata;
	}

	private String createStoreFileName(String originalFileName, String folderName) {
		String ext = extractExt(originalFileName);
		String fileName = extractFileName(originalFileName);
		String nowTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
		String uuid = UUID.randomUUID().toString();
		log.info("fileName = {} ", fileName);
		return folderName + nowTime + fileName + uuid + "." + ext;
	}

	private String extractExt(String originalFileName) {
		int pos = originalFileName.lastIndexOf(".");
		String ext = originalFileName.substring(pos + 1);
		return ext;
	}

	private String extractFileName(String originalFileName) {
		int pos = originalFileName.lastIndexOf(".");
		String fileName = originalFileName.substring(0, pos);
		return fileName;
	}
}