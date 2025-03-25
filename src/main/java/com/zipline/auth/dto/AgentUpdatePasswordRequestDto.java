package com.zipline.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AgentUpdatePasswordRequestDto {

	private String currentPassword;
	private String newPassword;

}
