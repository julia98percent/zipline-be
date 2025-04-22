package com.zipline.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zipline.entity.customer.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	Optional<Customer> findByUidAndDeletedAtIsNull(Long customerUid);

	@Query("SELECT c FROM Customer c WHERE c.user.uid = :userUID AND c.deletedAt IS NULL ORDER BY c.uid DESC")
	Page<Customer> findByUserUidAndDeletedAtIsNull(Long userUID, Pageable pageable);
}

