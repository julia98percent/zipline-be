package com.zipline.global.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContractFilterRequestDTO {
	private String period;
	private String status;
	private String category;
	private String customerName;
	private String address;
}
