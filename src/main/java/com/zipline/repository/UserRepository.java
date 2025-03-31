package com.zipline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zipline.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	@Query("SELECT user FROM User user WHERE user.id = :id")
	Optional<User> findByLoginId(String id);

	boolean existsById(String id);
}