package com.zipline.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.auth.entity.Agent;

public interface AgentRepository extends JpaRepository<Agent, Long> {
	Optional<Agent> findById(String id);

	boolean existsById(String id);
}
