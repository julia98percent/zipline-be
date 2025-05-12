package com.zipline.repository.customer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zipline.entity.customer.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, QCustomerRepository {
	Optional<Customer> findByUidAndDeletedAtIsNull(Long customerUid);

	boolean existsByUidAndUserUidAndDeletedAtIsNull(Long customerUid, Long userUid);

	Optional<Customer> findByUidAndUserUidAndDeletedAtIsNull(Long customerUid, Long userUid);

	@Query("SELECT c FROM Customer c WHERE c.user.uid = :userUid " +
		"AND SUBSTRING(c.birthday, 5) = :mmdd " +
		"AND c.deletedAt IS NULL")
	List<Customer> findCustomersWithBirthdayToday(Long userUid, String mmdd);

	@Query(value = "SELECT DISTINCT c FROM Customer c " +
		"LEFT JOIN FETCH c.labelCustomers lc " +
		"LEFT JOIN FETCH lc.label " +
		"WHERE c.user.uid = :userUid AND c.deletedAt IS NULL",
		countQuery = "SELECT COUNT(c) FROM Customer c WHERE c.user.uid = :userUid AND c.deletedAt IS NULL")
	Page<Customer> findByUserUidWithLabels(Long userUid, Pageable pageable);
}