package com.zipline.service.counsel;

import java.util.Map;

import com.zipline.global.response.ApiResponse;
import com.zipline.service.counsel.dto.request.CounselCreateRequestDTO;
import com.zipline.service.counsel.dto.request.CounselModifyRequestDTO;
import com.zipline.service.counsel.dto.response.CounselResponseDTO;

public interface CounselService {

	ApiResponse<Map<String, Long>> createCounsel(Long customerUid, CounselCreateRequestDTO requestDTO, Long userUid);

	ApiResponse<CounselResponseDTO> getCounsel(Long counselUid, Long userUid);

	ApiResponse<Map<String, Long>> modifyCounsel(Long counselUid, CounselModifyRequestDTO requestDTO, Long userUid);

	ApiResponse<Void> deleteCounsel(Long counselUid, Long userUid);
}