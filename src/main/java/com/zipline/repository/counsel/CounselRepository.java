package com.zipline.repository.counsel;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.counsel.Counsel;

public interface CounselRepository extends JpaRepository<Counsel, Long> {
	Optional<Counsel> findByUidAndDeletedAtIsNull(Long uid);
}