package com.zipline.repository.user;

import com.zipline.entity.user.User;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
	@Query("SELECT user FROM User user WHERE user.id = :id")
	Optional<User> findByLoginId(@Param("id") String id);

	Optional<User> findByNameAndEmail(String name, String email);

	boolean existsById(String id);

	List<User> findByDeletedAtIsNull();

}