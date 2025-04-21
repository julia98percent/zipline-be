package com.zipline.dto.counsel;

import java.time.LocalDateTime;

import com.zipline.entity.counsel.Counsel;

import lombok.Getter;

@Getter
public class CounselListResponseDTO {

	private Long counselUid;
	private String title;
	private LocalDateTime counselDate;

	public CounselListResponseDTO(Counsel counsel) {
		this.counselUid = counsel.getUid();
		this.title = counsel.getTitle();
		this.counselDate = counsel.getCounselDate();
	}
}
