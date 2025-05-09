package com.zipline.service.region;

import java.util.List;

import com.zipline.service.task.dto.TaskResponseDto;
import com.zipline.service.region.dto.RegionDTO;

public interface RegionCodeService {
	TaskResponseDto crawlAndSaveRegions(Boolean useProxy);

	TaskResponseDto crawlAndSaveRegionsForRegion(Boolean useProxy, Long cortarNo);
}
