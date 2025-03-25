package com.zipline.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.auth.entity.Agents;

public interface AgentsRepository extends JpaRepository<Agents, Long> {
	Optional<Agents> findById(String id);

	boolean existsById(String id);
}
