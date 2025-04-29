package com.zipline.repository.customer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zipline.entity.customer.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, QCustomerRepository {
	Optional<Customer> findByUidAndDeletedAtIsNull(Long customerUid);

	boolean existsByUidAndUserUidAndDeletedAtIsNull(Long customerUid, Long userUid);

	Optional<Customer> findByUidAndUserUidAndDeletedAtIsNull(Long customerUid, Long userUid);
}

