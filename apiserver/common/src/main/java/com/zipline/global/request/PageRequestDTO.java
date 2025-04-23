package com.zipline.global.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.Setter;

@Setter
public class PageRequestDTO {

	private int page;
	private int size;

	private int getPage() {
		if (this.page <= 0) {
			return 0;
		}
		return this.page - 1;
	}

	private Integer getSize() {
		if (this.size <= 0 || this.size > 100) {
			return 20;
		}
		return size;
	}

	public Pageable toPageable() {
		return PageRequest.of(getPage(), getSize());
	}
}