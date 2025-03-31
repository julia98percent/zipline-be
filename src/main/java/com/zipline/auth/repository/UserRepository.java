package com.zipline.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.auth.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findById(String id);

	boolean existsById(String id);
}
