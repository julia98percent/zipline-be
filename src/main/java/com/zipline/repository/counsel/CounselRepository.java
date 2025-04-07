package com.zipline.repository.counsel;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.counsel.Counsel;

public interface CounselRepository extends JpaRepository<Counsel, Long> {
}