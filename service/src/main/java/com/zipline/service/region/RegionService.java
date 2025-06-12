package com.zipline.service.region;

import com.zipline.global.response.ApiResponse;
import com.zipline.service.region.dto.FlatRegionDTO;

import java.util.List;

public interface RegionService {
    public ApiResponse<List<FlatRegionDTO>> getChildrenRegions(Long cortaNo);
}
