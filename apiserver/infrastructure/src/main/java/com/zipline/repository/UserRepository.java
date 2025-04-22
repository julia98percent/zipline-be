package com.zipline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zipline.entity.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
	@Query("SELECT user FROM User user WHERE user.id = :id")
	Optional<User> findByLoginId(@Param("id") String id);

	Optional<User> findByNameAndEmail(String name, String email);

	boolean existsById(String id);
}