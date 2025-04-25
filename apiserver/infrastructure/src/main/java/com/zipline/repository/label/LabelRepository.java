package com.zipline.repository.label;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.label.Label;

public interface LabelRepository extends JpaRepository<Label, Long> {
	boolean existsByUserUidAndName(Long userUid, String name);
}
