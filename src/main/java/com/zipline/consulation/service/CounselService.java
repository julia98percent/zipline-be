package com.zipline.consulation.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.consulation.dto.CounselCreateRequestDTO;
import com.zipline.consulation.entity.Counsel;
import com.zipline.consulation.entity.CounselDetail;
import com.zipline.consulation.repository.CounselRepository;
import com.zipline.entity.Customer;
import com.zipline.entity.User;
import com.zipline.global.common.response.ApiResponse;
import com.zipline.global.exception.custom.UserNotFoundException;
import com.zipline.global.exception.custom.customer.CustomerNotFoundException;
import com.zipline.repository.CustomerRepository;
import com.zipline.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CounselService {

	private final CounselRepository counselRepository;
	private final CustomerRepository customerRepository;
	private final UserRepository userRepository;

	@Transactional
	public ApiResponse<Map<String, Long>> createCounsel(Long customerUid, CounselCreateRequestDTO requestDTO,
		Long userUid) {
		Customer savedCustomer = customerRepository.findByUidAndIsDeletedFalse(customerUid)
			.orElseThrow(() -> new CustomerNotFoundException("해당하는 고객을 찾을 수 없습니다.",
				HttpStatus.BAD_REQUEST));
		User savedUser = userRepository.findById(userUid)
			.orElseThrow(() -> new UserNotFoundException("해당하는 사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		Counsel counsel = new Counsel(requestDTO.getTitle(), requestDTO.getCounselDate(), LocalDateTime.now(),
			null, null, savedUser, savedCustomer);
		for (CounselCreateRequestDTO.CounselDetailDTO counselDetailDTO : requestDTO.getCounselDetails()) {
			counsel.addDetail(new CounselDetail(counselDetailDTO.getQuestion(), counselDetailDTO.getAnswer(), counsel));
		}

		Counsel savedCounsel = counselRepository.save(counsel);
		return ApiResponse.create("상담 생성에 성공하였습니다.", Collections.singletonMap("counselUid", savedCounsel.getUid()));
	}
}
