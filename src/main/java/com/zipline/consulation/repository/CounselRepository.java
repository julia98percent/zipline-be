package com.zipline.consulation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.consulation.entity.Counsel;

public interface CounselRepository extends JpaRepository<Counsel, Long> {
}