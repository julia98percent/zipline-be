package com.zipline.repository.label;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.label.LabelCustomer;

public interface LabelCustomerRepository extends JpaRepository<LabelCustomer, Long> {
}
