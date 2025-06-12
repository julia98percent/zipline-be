package com.zipline.repository.customer;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.zipline.entity.customer.Customer;
import com.zipline.global.request.CustomerFilterRequestDTO;

public interface QCustomerRepository {
	Page<Customer> findByUserUidAndDeletedAtIsNullWithFilters(Long userUid,
		CustomerFilterRequestDTO customerFilterRequestDTO,
		Pageable pageable);

	List<Customer> findByNameAndPhoneNoPairs(Set<String> keySet);
}