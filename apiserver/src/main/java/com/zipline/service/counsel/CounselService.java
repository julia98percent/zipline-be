package com.zipline.service.counsel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.dto.counsel.CounselCreateRequestDTO;
import com.zipline.dto.counsel.CounselModifyRequestDTO;
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
import com.zipline.repository.counsel.CounselDetailRepository;
import com.zipline.repository.counsel.CounselRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CounselService {

	private final CounselRepository counselRepository;
	private final CustomerRepository customerRepository;
	private final UserRepository userRepository;
	private final CounselDetailRepository counselDetailRepository;

	@Transactional
	public ApiResponse<Map<String, Long>> createCounsel(Long customerUid, CounselCreateRequestDTO requestDTO,
		Long userUid) {
		Customer savedCustomer = customerRepository.findByUidAndIsDeletedFalse(customerUid)
			.orElseThrow(() -> new CustomerNotFoundException("해당하는 고객을 찾을 수 없습니다.",
				HttpStatus.BAD_REQUEST));
		User savedUser = userRepository.findById(userUid)
			.orElseThrow(() -> new UserNotFoundException("해당하는 사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		LocalDateTime createdAt = LocalDateTime.now();
		Counsel counsel = new Counsel(requestDTO.getTitle(), requestDTO.getCounselDate(), createdAt,
			null, null, savedUser, savedCustomer);
		for (CounselCreateRequestDTO.CounselDetailDTO counselDetailDTO : requestDTO.getCounselDetails()) {
			counsel.addDetail(
				new CounselDetail(counselDetailDTO.getQuestion(), counselDetailDTO.getAnswer(), counsel, createdAt,
					null));
		}

		Counsel savedCounsel = counselRepository.save(counsel);
		return ApiResponse.create("상담 생성에 성공하였습니다.", Collections.singletonMap("counselUid", savedCounsel.getUid()));
	}

	@Transactional(readOnly = true)
	public ApiResponse<CounselResponseDTO> getCounsel(Long counselUid, Long userUid) {
		Counsel savedCounsel = counselRepository.findByUidAndDeletedAtIsNull(counselUid)
			.orElseThrow(() -> new CounselNotFoundException("해당하는 상담을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		if (!savedCounsel.getUser().getUid().equals(userUid)) {
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}

		List<CounselDetail> savedCounselDetails = counselDetailRepository.findByCounselUidAndDeletedAtIsNull(
			counselUid);
		CounselResponseDTO counselResponseDTO = new CounselResponseDTO(savedCounsel, savedCounselDetails);
		return ApiResponse.ok("상담 상세 조회 성공", counselResponseDTO);
	}

	@Transactional
	public ApiResponse<Map<String, Long>> modifyCounsel(Long counselUid, CounselModifyRequestDTO requestDTO,
		Long userUid) {
		Counsel savedCounsel = counselRepository.findByUidAndDeletedAtIsNull(counselUid)
			.orElseThrow(() -> new CounselNotFoundException("해당하는 상담을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));

		List<CounselDetail> savedCounselDetails = counselDetailRepository.findByCounselUidAndDeletedAtIsNull(
			counselUid);
		if (!savedCounsel.getUser().getUid().equals(userUid)) {
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}

		LocalDateTime now = LocalDateTime.now();
		savedCounsel.update(requestDTO.getTitle(), requestDTO.getCounselDate(), now);
		savedCounselDetails.forEach(detail -> detail.delete(now));

		List<CounselDetail> counselDetails = new ArrayList<>();
		for (CounselModifyRequestDTO.CounselDetailDTO counselDetailDTO : requestDTO.getCounselDetails()) {
			counselDetails.add(
				new CounselDetail(counselDetailDTO.getQuestion(), counselDetailDTO.getAnswer(), savedCounsel, now,
					null));
		}

		counselDetailRepository.saveAll(counselDetails);
		return ApiResponse.ok("상담 수정에 성공하였습니다.", Collections.singletonMap("counselUid", savedCounsel.getUid()));
	}

	@Transactional
	public ApiResponse<Void> deleteCounsel(Long counselUid, Long userUid) {
		Counsel savedCounsel = counselRepository.findByUidAndDeletedAtIsNull(counselUid)
			.orElseThrow(() -> new CounselNotFoundException("해당하는 상담을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		if (!savedCounsel.getUser().getUid().equals(userUid)) {
			throw new PermissionDeniedException("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}
		List<CounselDetail> savedCounselDetails = counselDetailRepository.findByCounselUidAndDeletedAtIsNull(
			counselUid);
		LocalDateTime deletedAt = LocalDateTime.now();
		savedCounsel.delete(deletedAt);

		savedCounselDetails.forEach(counselDetail -> counselDetail.delete(deletedAt));
		return ApiResponse.ok("상담 삭제에 성공하였습니다.");
	}
}
