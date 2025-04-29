package com.zipline.global.request;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class CounselFilterRequestDTO {
	private String search;
	private LocalDate startDate;
	private LocalDate endDate;
}
