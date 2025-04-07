package com.zipline.service.counsel;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.dto.counsel.CounselCreateRequestDTO;
import com.zipline.dto.counsel.CounselResponseDTO;
import com.zipline.entity.Customer;
import com.zipline.entity.User;
import com.zipline.entity.counsel.Counsel;
import com.zipline.entity.counsel.CounselDetail;
import com.zipline.global.common.response.ApiResponse;
import com.zipline.global.exception.custom.CounselNotFoundException;
import com.zipline.global.exception.custom.PermissionDeniedException;
import com.zipline.global.exception.custom.UserNotFoundException;
import com.zipline.global.exception.custom.customer.CustomerNotFoundException;
import com.zipline.repository.CustomerRepository;
import com.zipline.repository.UserRepository;
import com.zipline.repository.counsel.CounselRepository;

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

	@Transactional(readOnly = true)
	public ApiResponse<CounselResponseDTO> getCounsel(Long counselUid, Long userUid) {
		Counsel savedCounsel = counselRepository.findById(counselUid)
			.orElseThrow(() -> new CounselNotFoundException("해당하는 상담을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		if (!savedCounsel.getUser().getUid().equals(userUid)) {
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}
		CounselResponseDTO counselResponseDTO = new CounselResponseDTO(savedCounsel);
		return ApiResponse.ok("상담 상세 조회 성공", counselResponseDTO);
	}
}
