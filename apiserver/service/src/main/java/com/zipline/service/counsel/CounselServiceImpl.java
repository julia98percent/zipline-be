package com.zipline.service.counsel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.entity.counsel.Counsel;
import com.zipline.entity.counsel.CounselDetail;
import com.zipline.entity.customer.Customer;
import com.zipline.entity.user.User;
import com.zipline.global.exception.auth.AuthException;
import com.zipline.global.exception.counsel.CounselException;
import com.zipline.global.exception.customer.CustomerException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.global.exception.auth.errorcode.AuthErrorCode;
import com.zipline.global.exception.counsel.errorcode.CounselErrorCode;
import com.zipline.global.exception.customer.errorcode.CustomerErrorCode;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.response.ApiResponse;
import com.zipline.repository.counsel.CounselDetailRepository;
import com.zipline.repository.counsel.CounselRepository;
import com.zipline.repository.customer.CustomerRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.counsel.dto.request.CounselCreateRequestDTO;
import com.zipline.service.counsel.dto.request.CounselModifyRequestDTO;
import com.zipline.service.counsel.dto.response.CounselResponseDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CounselServiceImpl implements CounselService {

	private final CounselRepository counselRepository;
	private final CustomerRepository customerRepository;
	private final UserRepository userRepository;
	private final CounselDetailRepository counselDetailRepository;

	@Transactional
	public ApiResponse<Map<String, Long>> createCounsel(Long customerUid, CounselCreateRequestDTO requestDTO,
		Long userUid) {
		Customer savedCustomer = customerRepository.findByUidAndDeletedAtIsNull(customerUid)
			.orElseThrow(() -> new CustomerException(CustomerErrorCode.CUSTOMER_NOT_FOUND));
		User savedUser = userRepository.findById(userUid)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		Counsel counsel = new Counsel(requestDTO.getTitle(), requestDTO.getCounselDate(), null, null, savedUser,
			savedCustomer, null);
		for (CounselCreateRequestDTO.CounselDetailDTO counselDetailDTO : requestDTO.getCounselDetails()) {
			counsel.addDetail(
				new CounselDetail(counselDetailDTO.getQuestion(), counselDetailDTO.getAnswer(), counsel));
		}

		Counsel savedCounsel = counselRepository.save(counsel);
		return ApiResponse.create("상담 생성에 성공하였습니다.", Collections.singletonMap("counselUid", savedCounsel.getUid()));
	}

	@Transactional(readOnly = true)
	public ApiResponse<CounselResponseDTO> getCounsel(Long counselUid, Long userUid) {
		Counsel savedCounsel = counselRepository.findByUidAndDeletedAtIsNull(counselUid)
			.orElseThrow(() -> new CounselException(CounselErrorCode.COUNSEL_NOT_FOUND));

		if (!savedCounsel.getUser().getUid().equals(userUid)) {
			throw new AuthException(AuthErrorCode.PERMISSION_DENIED);
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
			.orElseThrow(() -> new CounselException(CounselErrorCode.COUNSEL_NOT_FOUND));

		List<CounselDetail> savedCounselDetails = counselDetailRepository.findByCounselUidAndDeletedAtIsNull(
			counselUid);
		if (!savedCounsel.getUser().getUid().equals(userUid)) {
			throw new AuthException(AuthErrorCode.PERMISSION_DENIED);
		}
		LocalDateTime deletedAt = LocalDateTime.now();
		savedCounsel.update(requestDTO.getTitle(), requestDTO.getCounselDate());
		savedCounselDetails.forEach(detail -> detail.delete(deletedAt));

		List<CounselDetail> counselDetails = new ArrayList<>();
		for (CounselModifyRequestDTO.CounselDetailDTO counselDetailDTO : requestDTO.getCounselDetails()) {
			counselDetails.add(
				new CounselDetail(counselDetailDTO.getQuestion(), counselDetailDTO.getAnswer(), savedCounsel));
		}

		counselDetailRepository.saveAll(counselDetails);
		return ApiResponse.ok("상담 수정에 성공하였습니다.", Collections.singletonMap("counselUid", savedCounsel.getUid()));
	}

	@Transactional
	public ApiResponse<Void> deleteCounsel(Long counselUid, Long userUid) {
		Counsel savedCounsel = counselRepository.findByUidAndDeletedAtIsNull(counselUid)
			.orElseThrow(() -> new CounselException(CounselErrorCode.COUNSEL_NOT_FOUND));
		if (!savedCounsel.getUser().getUid().equals(userUid)) {
			throw new AuthException(AuthErrorCode.PERMISSION_DENIED);
		}
		List<CounselDetail> savedCounselDetails = counselDetailRepository.findByCounselUidAndDeletedAtIsNull(
			counselUid);
		LocalDateTime deletedAt = LocalDateTime.now();
		savedCounsel.delete(deletedAt);

		savedCounselDetails.forEach(counselDetail -> counselDetail.delete(deletedAt));
		return ApiResponse.ok("상담 삭제에 성공하였습니다.");
	}
}
