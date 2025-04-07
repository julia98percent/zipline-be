package com.zipline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zipline.entity.AgentProperty;

@Repository
public interface AgentPropertyRepository extends JpaRepository<AgentProperty, Long> {
}
