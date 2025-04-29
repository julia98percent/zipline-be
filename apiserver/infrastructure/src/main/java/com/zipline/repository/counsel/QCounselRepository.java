package com.zipline.repository.counsel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.zipline.entity.counsel.Counsel;
import com.zipline.global.request.CounselFilterRequestDTO;

public interface QCounselRepository {
	Page<Counsel> findByUserUidAndDeletedAtIsNullWithFiltering(Long userUid, Pageable pageable,
		CounselFilterRequestDTO filterRequestDTO);

	Page<Counsel> findByUserUidAndDeletedAtIsNullWithSortType(Long userUid, Pageable pageable, String sortType);
}
