package com.zipline.repository.customer;

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

	@Query(value = "SELECT c FROM Customer c " +
		"WHERE c.user.uid = :userUid AND c.deletedAt IS NULL",
		countQuery = "SELECT COUNT(c) FROM Customer c WHERE c.user.uid = :userUid AND c.deletedAt IS NULL")
	Page<Customer> findByUserUidWithLabels(Long userUid, Pageable pageable);

	boolean existsByUidAndUserUidAndDeletedAtIsNull(Long customerUid, Long userUid);

	Optional<Customer> findByUidAndUserUidAndDeletedAtIsNull(Long customerUid, Long userUid);
}

