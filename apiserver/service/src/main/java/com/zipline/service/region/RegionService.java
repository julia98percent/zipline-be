package com.zipline.service.region;

import com.zipline.global.response.ApiResponse;
import com.zipline.service.region.dto.RegionResponseDTO;

public interface RegionService {
    public ApiResponse<RegionResponseDTO> getLv1Regions();
    public ApiResponse<RegionResponseDTO> getChildrenRegions(Long cortaNo);
}
