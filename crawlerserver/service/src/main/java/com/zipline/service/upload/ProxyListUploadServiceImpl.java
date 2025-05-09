package com.zipline.service.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.zipline.global.exception.file.FileUploadException;
import com.zipline.global.exception.file.errorcode.FileErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProxyListUploadServiceImpl implements ProxyListUploadService {

	private static final String UPLOAD_DIR = "/app/config/";

	public String saveFile(MultipartFile file) {
		boolean isNewFile = false;

		if (file.isEmpty()) {
			throw new FileUploadException(FileErrorCode.FILE_EMPTY);
		}

		// 파일 내용 유효성 검사
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			String line;
			int lineNumber = 0;
			while ((line = reader.readLine()) != null) {
				lineNumber++;
				// 공백을 제거하기 위해 라인 트림
				String trimmedLine = line.trim();
				// 빈 라인 건너뛰기
				if (trimmedLine.isEmpty()) {
					continue;
				}
				// 보이지 않는 문자 제거 및 공백 정규화
				trimmedLine = trimmedLine.replaceAll("\\s+", "").replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "");
				// 정리 후 빈 라인이면 건너뛰기
				if (trimmedLine.isEmpty()) {
					continue;
				}
				// 더 유연한 IP:PORT 검증 패턴
				if (!trimmedLine.matches("^(?:\\d{1,3}\\.){3}\\d{1,3}:\\d+$")) {
					throw new FileUploadException(FileErrorCode.FILE_WRONG_TEXT_FORMATE);
				}
			}
		} catch (IOException e) {
			throw new FileUploadException(FileErrorCode.FILE_READ_FAILED);
		}

		// 디렉토리 존재 여부 확인
		File directory = new File(UPLOAD_DIR);
		if (!directory.exists() && !directory.mkdirs()) {
			throw new FileUploadException(FileErrorCode.FILE_DIR_CREATE_FAILED);
		}

		// 파일 저장
		try {
			Path filePath = Paths.get(UPLOAD_DIR + "proxy-list.txt");
			// 파일이 이미 존재하면 삭제
			if (Files.exists(filePath)) {
				Files.delete(filePath);
				isNewFile = false;
			} else {
				isNewFile = true;
			}
			Files.write(filePath, file.getBytes());
		} catch (IOException e) {
			throw new FileUploadException(FileErrorCode.FILE_SAVE_FAILED);
		}
		return isNewFile ? "proxy-list.txt 파일이 성공적으로 생성되었습니다" : "proxy-list.txt 파일이 성공적으로 업데이트 되었습니다";
	}
}
