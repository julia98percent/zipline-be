package com.zipline.service.label;

import com.zipline.service.label.dto.request.LabelRequestDTO;
import com.zipline.service.label.dto.response.LabelResponseDTO;

public interface LabelService {
	void createLabel(Long userUid, LabelRequestDTO dto);

	LabelResponseDTO modifyLabel(Long userUid, Long labelUid, LabelRequestDTO dto);
}
