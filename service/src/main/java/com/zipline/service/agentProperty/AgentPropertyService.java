package com.zipline.service.agentProperty;

import com.zipline.global.request.AgentPropertyFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.service.agentProperty.dto.request.AgentPropertyRequestDTO;
import com.zipline.service.agentProperty.dto.response.AgentPropertyListResponseDTO;
import com.zipline.service.agentProperty.dto.response.AgentPropertyResponseDTO;
import io.micrometer.core.annotation.Timed;

public interface AgentPropertyService {

  @Timed
  AgentPropertyResponseDTO getProperty(Long propertyUid, Long userUid);

  @Timed
  AgentPropertyResponseDTO registerProperty(AgentPropertyRequestDTO agentPropertyRequestDTO,
      Long userUid);

  @Timed
  AgentPropertyResponseDTO modifyProperty(AgentPropertyRequestDTO agentPropertyRequestDTO,
      Long propertyUid,
      Long userUid);

  @Timed
  void deleteProperty(Long propertyUid, Long userUid);

  @Timed
  AgentPropertyListResponseDTO getAgentPropertyList(PageRequestDTO pageRequestDTO, Long userUid,
      AgentPropertyFilterRequestDTO detailFilter);
}