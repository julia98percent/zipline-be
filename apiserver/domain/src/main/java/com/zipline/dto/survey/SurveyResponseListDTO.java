package com.zipline.dto.survey;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class SurveyResponseListDTO {
	private List<SurveyResponseListDataDTO> surveyResponses;
	private int page;
	private int size;
	private int totalElements;
	private int totalPages;
	private boolean hasNext;

	public SurveyResponseListDTO(List<SurveyResponseListDataDTO> surveyResponses, Page page) {
		this.surveyResponses = surveyResponses;
		this.page = page.getNumber() + 1;
		this.size = page.getSize();
		this.totalElements = (int)page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.hasNext = page.hasNext();
	}

	@Getter
	public static class SurveyResponseListDataDTO {
		private Long surveyResponseUid;
		private String name;
		private String phoneNumber;
		private LocalDateTime submittedAt;

		public SurveyResponseListDataDTO(Long surveyResponseUid, String name, String phoneNumber,
			LocalDateTime submittedAt) {
			this.surveyResponseUid = surveyResponseUid;
			this.name = name;
			this.phoneNumber = phoneNumber;
			this.submittedAt = submittedAt;
		}
	}
}
