package com.zipline.service.counsel.dto.response;

import java.time.LocalDate;

import com.zipline.entity.counsel.Counsel;

import lombok.Getter;

@Getter
public class CounselHistoryResponseDTO {

	private Long counselUid;
	private String counselTitle;
	private LocalDate counselDate;
	private String customerName;
	private String customerPhoneNo;

	public CounselHistoryResponseDTO(Counsel counsel) {
		this.counselUid = counsel.getUid();
		this.counselTitle = counsel.getTitle();
		this.counselDate = LocalDate.from(counsel.getCounselDate());
		this.customerName = counsel.getCustomer().getName();
		this.customerPhoneNo = counsel.getCustomer().getPhoneNo();
	}
}
