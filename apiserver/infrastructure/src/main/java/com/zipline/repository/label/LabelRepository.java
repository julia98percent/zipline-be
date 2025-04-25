package com.zipline.repository.label;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.label.Label;

public interface LabelRepository extends JpaRepository<Label, Long> {
	boolean existsByUserUidAndName(Long userUid, String name);

	Optional<Label> findByUidAndUserUid(Long labelUid, Long userUid);
}
