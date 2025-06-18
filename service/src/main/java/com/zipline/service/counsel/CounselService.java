package com.zipline.service.counsel;

import com.zipline.global.request.CounselFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.service.counsel.dto.request.CounselCreateRequestDTO;
import com.zipline.service.counsel.dto.request.CounselModifyRequestDTO;
import com.zipline.service.counsel.dto.response.CounselPageResponseDTO;
import com.zipline.service.counsel.dto.response.CounselResponseDTO;
import io.micrometer.core.annotation.Timed;
import java.util.Map;

public interface CounselService {

  @Timed
  Map<String, Long> createCounsel(Long customerUid, CounselCreateRequestDTO requestDTO,
      Long userUid);

  @Timed
  CounselResponseDTO getCounsel(Long counselUid, Long userUid);

  @Timed
  Map<String, Long> modifyCounsel(Long counselUid, CounselModifyRequestDTO requestDTO,
      Long userUid);

  @Timed
  void deleteCounsel(Long counselUid, Long userUid);

  @Timed
  CounselPageResponseDTO getCounsels(PageRequestDTO pageRequestDTO,
      CounselFilterRequestDTO filterRequestDTO,
      Long userUid);

  @Timed
  CounselPageResponseDTO getDashBoardCounsels(PageRequestDTO pageRequestDTO, String sortType,
      Long userUid);

  @Timed
  CounselPageResponseDTO getPropertyCounselHistories(PageRequestDTO pageRequestDTO,
      Long propertyUid, Long userUid);
}