package com.zipline.repository.label;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.label.Label;

public interface LabelRepository extends JpaRepository<Label, Long> {
	boolean existsByUserUidAndNameAndDeletedAtIsNull(Long userUid, String name);

	Optional<Label> findByUidAndUserUidAndDeletedAtIsNull(Long labelUid, Long userUid);

	List<Label> findAllByUserUidAndDeletedAtIsNull(Long userUid);

	List<Label> findAllByUidInAndUserUidAndDeletedAtIsNull(List<Long> labelUids, Long userUid);
}
