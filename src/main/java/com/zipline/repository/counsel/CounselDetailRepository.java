package com.zipline.repository.counsel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.counsel.CounselDetail;

public interface CounselDetailRepository extends JpaRepository<CounselDetail, Long> {
	List<CounselDetail> findByCounselUidAndDeletedAtIsNull(Long counselUid);
}