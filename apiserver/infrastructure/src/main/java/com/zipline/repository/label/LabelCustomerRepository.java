package com.zipline.repository.label;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.label.LabelCustomer;

public interface LabelCustomerRepository extends JpaRepository<LabelCustomer, Long> {
	List<LabelCustomer> findAllByCustomerUid(Long uid);
}
