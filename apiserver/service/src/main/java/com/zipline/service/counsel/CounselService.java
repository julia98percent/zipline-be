package com.zipline.service.counsel;

import java.util.Map;

import com.zipline.dto.counsel.CounselCreateRequestDTO;
import com.zipline.dto.counsel.CounselModifyRequestDTO;
import com.zipline.dto.counsel.CounselResponseDTO;
import com.zipline.global.response.ApiResponse;

public interface CounselService {

	ApiResponse<Map<String, Long>> createCounsel(Long customerUid, CounselCreateRequestDTO requestDTO, Long userUid);

	ApiResponse<CounselResponseDTO> getCounsel(Long counselUid, Long userUid);

	ApiResponse<Map<String, Long>> modifyCounsel(Long counselUid, CounselModifyRequestDTO requestDTO, Long userUid);

	ApiResponse<Void> deleteCounsel(Long counselUid, Long userUid);
}