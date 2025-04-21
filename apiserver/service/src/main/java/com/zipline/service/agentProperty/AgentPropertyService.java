package com.zipline.service.agentProperty;

import com.zipline.dto.PageRequestDTO;
import com.zipline.dto.agentProperty.AgentPropertyListResponseDTO;
import com.zipline.dto.agentProperty.AgentPropertyRequestDTO;
import com.zipline.dto.agentProperty.AgentPropertyResponseDTO;

public interface AgentPropertyService {

	AgentPropertyResponseDTO getProperty(Long propertyUid, Long userUid);

	AgentPropertyResponseDTO registerProperty(AgentPropertyRequestDTO agentPropertyRequestDTO, Long userUid);

	AgentPropertyResponseDTO modifyProperty(AgentPropertyRequestDTO agentPropertyRequestDTO, Long propertyUid,
		Long userUid);

	void deleteProperty(Long propertyUid, Long userUid);

	AgentPropertyListResponseDTO getAgentPropertyList(PageRequestDTO pageRequestDTO, Long userUid);
}
