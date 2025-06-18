package com.zipline.service.label;

import com.zipline.service.label.dto.request.LabelRequestDTO;
import com.zipline.service.label.dto.response.LabelListResponseDTO;
import com.zipline.service.label.dto.response.LabelResponseDTO;
import io.micrometer.core.annotation.Timed;

public interface LabelService {

  @Timed
  void createLabel(Long userUid, LabelRequestDTO dto);

  @Timed
  LabelResponseDTO modifyLabel(Long userUid, Long labelUid, LabelRequestDTO dto);

  @Timed
  void deleteLabel(Long userUid, Long labelUid);

  @Timed
  LabelListResponseDTO getLabelList(Long userUid);
}