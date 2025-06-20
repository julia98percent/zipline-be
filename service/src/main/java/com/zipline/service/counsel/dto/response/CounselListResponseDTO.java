package com.zipline.service.counsel.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zipline.entity.agentProperty.AgentProperty;
import com.zipline.entity.counsel.Counsel;

import lombok.Getter;

@Getter
public class CounselListResponseDTO {

	private Long counselUid;
	private String title;
	private String type;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String customerName;

	private LocalDateTime counselDate;
	private LocalDate dueDate;
	private Long propertyUid;
	private boolean completed;

	private CounselListResponseDTO(Long counselUid, String title, String type, String customerName,
		LocalDateTime counselDate, LocalDate dueDate, AgentProperty agentProperty, boolean completed) {
		this.counselUid = counselUid;
		this.title = title;
		this.type = type;
		this.customerName = customerName;
		this.counselDate = counselDate;
		this.dueDate = dueDate;
		this.propertyUid = agentProperty != null ? agentProperty.getUid() : null;
		this.completed = completed;
	}

	public static CounselListResponseDTO createWithoutCustomerName(Counsel counsel) {
		return new CounselListResponseDTO(counsel.getUid(), counsel.getTitle(), counsel.getType().getDescription(),
			null, counsel.getCounselDate(), counsel.getDueDate(), counsel.getAgentProperty(), counsel.isCompleted());
	}

	public static CounselListResponseDTO createWithCustomerName(Counsel counsel) {
		return new CounselListResponseDTO(counsel.getUid(), counsel.getTitle(), counsel.getType().getDescription(),
			counsel.getCustomer().getName(), counsel.getCounselDate(), counsel.getDueDate(),
			counsel.getAgentProperty(), counsel.isCompleted());
	}
}
