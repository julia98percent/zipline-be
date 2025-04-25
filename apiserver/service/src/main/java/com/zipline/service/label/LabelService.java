package com.zipline.service.label;

import com.zipline.service.label.dto.request.LabelRequestDTO;

public interface LabelService {
	void createLabel(Long userUid, LabelRequestDTO dto);
}
