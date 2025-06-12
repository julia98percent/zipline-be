package com.zipline.service.agentProperty;

import com.zipline.global.request.AgentPropertyFilterRequestDTO;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.service.agentProperty.dto.request.AgentPropertyRequestDTO;
import com.zipline.service.agentProperty.dto.response.AgentPropertyListResponseDTO;
import com.zipline.service.agentProperty.dto.response.AgentPropertyResponseDTO;

public interface AgentPropertyService {

	AgentPropertyResponseDTO getProperty(Long propertyUid, Long userUid);

	AgentPropertyResponseDTO registerProperty(AgentPropertyRequestDTO agentPropertyRequestDTO, Long userUid);

	AgentPropertyResponseDTO modifyProperty(AgentPropertyRequestDTO agentPropertyRequestDTO, Long propertyUid,
		Long userUid);

	void deleteProperty(Long propertyUid, Long userUid);

	AgentPropertyListResponseDTO getAgentPropertyList(PageRequestDTO pageRequestDTO, Long userUid,
		AgentPropertyFilterRequestDTO detailFilter);
}
