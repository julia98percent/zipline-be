package com.zipline.service.label;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.entity.label.Label;
import com.zipline.entity.user.User;
import com.zipline.global.exception.auth.AuthException;
import com.zipline.global.exception.auth.errorcode.AuthErrorCode;
import com.zipline.global.exception.label.LabelException;
import com.zipline.global.exception.label.errorcode.LabelErrorCode;
import com.zipline.global.exception.user.UserException;
import com.zipline.global.exception.user.errorcode.UserErrorCode;
import com.zipline.repository.label.LabelRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.label.dto.request.LabelRequestDTO;
import com.zipline.service.label.dto.response.LabelListResponseDTO;
import com.zipline.service.label.dto.response.LabelResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

	private final LabelRepository labelRepository;
	private final UserRepository userRepository;

	@Transactional
	public void createLabel(Long userUid, LabelRequestDTO labelRequestDTO) {
		User user = userRepository.findById(userUid)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		boolean exists = labelRepository.existsByUserUidAndNameAndDeletedAtIsNull(userUid, labelRequestDTO.getName());
		if (exists) {
			throw new LabelException(LabelErrorCode.LABEL_DUPLICATE);
		}

		Label label = labelRequestDTO.toEntity(user);
		labelRepository.save(label);
	}

	@Transactional
	public LabelResponseDTO modifyLabel(Long userUid, Long labelUid, LabelRequestDTO labelRequestDTO) {
		Label label = labelRepository.findByUidAndUserUidAndDeletedAtIsNull(labelUid, userUid)
			.orElseThrow(() -> new LabelException(LabelErrorCode.LABEL_NOT_FOUND));

		if (!label.getName().equals(labelRequestDTO.getName()) &&
			labelRepository.existsByUserUidAndNameAndDeletedAtIsNull(userUid, labelRequestDTO.getName())) {
			throw new LabelException(LabelErrorCode.LABEL_DUPLICATE);
		}

		label.updateName(labelRequestDTO.getName());

		return new LabelResponseDTO(label);
	}

	@Transactional
	public void deleteLabel(Long userUid, Long labelUid) {
		Label label = labelRepository.findByUidAndUserUidAndDeletedAtIsNull(labelUid, userUid)
			.orElseThrow(() -> new LabelException(LabelErrorCode.LABEL_NOT_FOUND));

		if (!label.getUser().getUid().equals(userUid)) {
			throw new AuthException(AuthErrorCode.PERMISSION_DENIED);
		}
		label.delete(LocalDateTime.now());
	}

	@Transactional(readOnly = true)
	public LabelListResponseDTO getLabelList(Long userUid) {
		List<Label> labelList = labelRepository.findAllByUserUidAndDeletedAtIsNull(userUid);
		List<LabelResponseDTO> labels = labelList.stream()
			.map(LabelResponseDTO::new)
			.toList();
		return new LabelListResponseDTO(labels);
	}
}
