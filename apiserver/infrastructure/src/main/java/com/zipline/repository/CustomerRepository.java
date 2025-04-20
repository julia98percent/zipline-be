package com.zipline.repository;

import java.util.Optional;

import com.zipline.entity.customer.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	Optional<Customer> findByUidAndIsDeletedFalse(Long customerUid);

	@Query("SELECT c FROM Customer c WHERE c.user.uid = :userUID AND c.isDeleted = :isDeleted order by c.uid DESC")
	Page<Customer> findByUserUidAndIsDeleted(Long userUID, boolean isDeleted, Pageable pageable);
}
