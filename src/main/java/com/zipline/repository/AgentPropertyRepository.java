package com.zipline.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zipline.entity.AgentProperty;

@Repository
public interface AgentPropertyRepository extends JpaRepository<AgentProperty, Long> {
	Optional<AgentProperty> findByUidAndIsDeletedFalse(Long propertyUid);

	@Query("SELECT a FROM AgentProperty a WHERE a.user.uid = :userUID AND a.isDeleted = :isDeleted ORDER BY a.uid")
	Page<AgentProperty> findByUserUidAndIsDeleted(Long userUID, boolean isDeleted, Pageable pageable);
}
