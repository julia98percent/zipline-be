package com.zipline.service.counsel;

import java.util.Map;

import com.zipline.service.counsel.dto.request.CounselCreateRequestDTO;
import com.zipline.service.counsel.dto.request.CounselModifyRequestDTO;
import com.zipline.service.counsel.dto.response.CounselResponseDTO;

public interface CounselService {

	Map<String, Long> createCounsel(Long customerUid, CounselCreateRequestDTO requestDTO, Long userUid);

	CounselResponseDTO getCounsel(Long counselUid, Long userUid);

	Map<String, Long> modifyCounsel(Long counselUid, CounselModifyRequestDTO requestDTO, Long userUid);

	void deleteCounsel(Long counselUid, Long userUid);
}