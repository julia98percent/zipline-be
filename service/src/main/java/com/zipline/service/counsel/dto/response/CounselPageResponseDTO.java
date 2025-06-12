package com.zipline.service.counsel.dto.response;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CounselPageResponseDTO<T> {
	private int page;
	private int size;
	private int totalElements;
	private int totalPages;
	private boolean hasNext;
	private T counsels;

	public CounselPageResponseDTO(Page page, T counsels) {
		this.page = page.getNumber() + 1;
		this.size = page.getSize();
		this.totalElements = (int)page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.hasNext = page.hasNext();
		this.counsels = counsels;
	}
}
