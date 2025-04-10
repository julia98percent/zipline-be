package com.zipline.service.publicItem;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

import com.zipline.global.exception.custom.publicItem.EmptyFileException;
import com.zipline.global.exception.custom.publicItem.WrongTextFormateException;
import com.zipline.global.exception.custom.publicItem.FailedDirCreationException;
import com.zipline.global.exception.custom.publicItem.FileReadException;
import com.zipline.global.exception.custom.publicItem.FileSaveException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ProxyListUploadService {

    private static final String UPLOAD_DIR = "/app/config/";

public String saveFile(MultipartFile file){
    boolean isNewFile = false;

    if (file.isEmpty()) {
        throw new EmptyFileException("파일이 비어 있습니다", HttpStatus.BAD_REQUEST);
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
                throw new WrongTextFormateException("Invalid file format. Line " + lineNumber + " must be in ip:port format: '" + line + "'", HttpStatus.BAD_REQUEST);
            }
        }
    } catch (IOException e) {
        throw new FileReadException("파일 읽기 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // 디렉토리 존재 여부 확인
    File directory = new File(UPLOAD_DIR);
    if (!directory.exists() && !directory.mkdirs()) {
        throw new FailedDirCreationException("파일 업로드를 위한 디렉토리 생성에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
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
        throw new FileSaveException("파일 저장 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
    } 
    return isNewFile ? "proxy-list.txt 파일이 성공적으로 생성되었습니다" : "proxy-list.txt 파일이 성공적으로 업데이트 되었습니다";
}
}
