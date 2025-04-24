package com.zipline.service.label;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zipline.entity.label.Label;
import com.zipline.entity.user.User;
import com.zipline.global.exception.custom.label.LabelNameDuplicatedException;
import com.zipline.global.exception.custom.user.UserNotFoundException;
import com.zipline.repository.label.LabelRepository;
import com.zipline.repository.user.UserRepository;
import com.zipline.service.label.dto.request.LabelRequestDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

	private final LabelRepository labelRepository;
	private final UserRepository userRepository;

	@Transactional
	public void createLabel(Long userUid, LabelRequestDTO labelRequestDTO) {
		User user = userRepository.findById(userUid)
			.orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		boolean exists = labelRepository.existsByUserUidAndName(userUid, labelRequestDTO.getName());
		if (exists) {
			throw new LabelNameDuplicatedException("이미 존재하는 라벨 이름입니다.", HttpStatus.BAD_REQUEST);
		}

		Label label = labelRequestDTO.toEntity(user);
		labelRepository.save(label);
	}
}
