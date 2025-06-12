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
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.zipline.global.config.S3Folder;
import com.zipline.global.exception.common.FileUploadException;
import com.zipline.global.exception.common.errorcode.CommonErrorCode;
import com.zipline.global.request.SurveyFileDTO;

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

	public Map<Long, SurveyFileDTO> uploadSurveyFiles(Map<Long, SurveyFileDTO> questionUidFileMap, S3Folder s3Folder) {
		Map<Long, SurveyFileDTO> fileUrlMap = new HashMap<>();

		for (Long questionUid : questionUidFileMap.keySet()) {
			SurveyFileDTO surveyFileDTO = questionUidFileMap.get(questionUid);
			fileValidator.validateSurveyFile(surveyFileDTO.getFile());
			String uploadedUrl = uploadFile(surveyFileDTO, s3Folder.getFolderPrefix());
			fileUrlMap.put(questionUid,
				SurveyFileDTO.createSurveyFileDTO(surveyFileDTO.getFileName(), surveyFileDTO.getFile(), uploadedUrl));
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
			log.error("File Upload Failed : name = {}, size = {}, error= {}", file.getOriginalFilename(),
				file.getSize(), e.getMessage());
			throw new FileUploadException(CommonErrorCode.FILE_UPLOAD_FAILED);
		}

		return amazonS3.getUrl(bucket, storeFileName).toString();
	}

	public String uploadFile(SurveyFileDTO surveyFileDTO, String folderPrefix) {
		MultipartFile file = surveyFileDTO.getFile();
		String storeFileName = createStoreFileName(surveyFileDTO.getFileName(), folderPrefix);

		ObjectMetadata metadata = createObjectMetadata(file);

		try (InputStream inputStream = file.getInputStream()) {
			amazonS3.putObject(new PutObjectRequest(bucket, storeFileName, inputStream, metadata));
		} catch (IOException e) {
			log.error("File Upload Failed : name = {}, size = {}, error= {}", file.getOriginalFilename(),
				file.getSize(), e.getMessage());
			throw new FileUploadException(CommonErrorCode.FILE_UPLOAD_FAILED);
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
		String nowTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
		String uuid = UUID.randomUUID().toString();
		return folderName + nowTime + uuid + "." + ext;
	}

	private String extractExt(String originalFileName) {
		int pos = originalFileName.lastIndexOf(".");
		String ext = originalFileName.substring(pos + 1);
		return ext;
	}
}