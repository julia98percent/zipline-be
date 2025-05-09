package com.zipline.service.upload;

import org.springframework.web.multipart.MultipartFile;

public interface ProxyListUploadService {

	/**
	 * 프록시 리스트 파일 저장 및 검증
	 *
	 * @param file 업로드된 MultipartFile
	 * @return 처리 결과 메시지 (생성 or 업데이트 성공)
	 */
	String saveFile(MultipartFile file);
}
