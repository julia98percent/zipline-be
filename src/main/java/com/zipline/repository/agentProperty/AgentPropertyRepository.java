package com.zipline.repository.agentProperty;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zipline.entity.agentProperty.AgentProperty;

@Repository
public interface AgentPropertyRepository extends JpaRepository<AgentProperty, Long> {
	Optional<AgentProperty> findByUidAndIsDeletedFalse(Long propertyUid);
}
