package com.zipline.service.counsel.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.zipline.entity.counsel.Counsel;
import com.zipline.entity.counsel.CounselDetail;

import lombok.Getter;

@Getter
public class CounselResponseDTO {
	private Long counselUid;
	private String title;
	private String type;
	private LocalDateTime counselDate;
	private LocalDate dueDate;
	private Long propertyUid;
	private boolean completed;
	private List<CounselDetailResponseDTO> counselDetails;

	public CounselResponseDTO(Counsel counsel, List<CounselDetail> counselDetails) {
		this.counselUid = counsel.getUid();
		this.title = counsel.getTitle();
		this.type = counsel.getType().getDescription();
		this.counselDate = counsel.getCounselDate();
		this.dueDate = counsel.getDueDate();
		this.propertyUid = (counsel.getAgentProperty() != null) ? counsel.getAgentProperty().getUid() : null;
		this.completed = counsel.isCompleted();
		this.counselDetails = counselDetails
			.stream()
			.map(CounselDetailResponseDTO::new)
			.collect(Collectors.toList());
	}

	@Getter
	public static class CounselDetailResponseDTO {
		private Long counselDetailUid;
		private String question;
		private String answer;

		public CounselDetailResponseDTO(CounselDetail counselDetail) {
			this.counselDetailUid = counselDetail.getUid();
			this.question = counselDetail.getQuestion();
			this.answer = counselDetail.getAnswer();
		}
	}
}
