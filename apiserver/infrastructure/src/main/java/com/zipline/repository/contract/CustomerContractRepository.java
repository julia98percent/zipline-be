package com.zipline.repository.contract;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zipline.entity.contract.CustomerContract;

@Repository
public interface CustomerContractRepository extends JpaRepository<CustomerContract, Long> {
	@Query("SELECT cc FROM CustomerContract cc Where cc.contract.uid IN :contractIds ORDER BY cc.contract.uid DESC")
	List<CustomerContract> findInContractUids(List<Long> contractIds);

	@Query("SELECT cc FROM CustomerContract cc WHERE cc.customer.uid = :customerUid AND cc.contract.user.uid =:userUid ORDER BY cc.contract.contractDate DESC")
	List<CustomerContract> findByCustomerUidAndUserUid(Long customerUid, Long userUid);

	List<CustomerContract> findAllByContractUid(Long contractUid);

	@Query("SELECT cc FROM CustomerContract cc JOIN FETCH cc.customer WHERE cc.contract.uid = :contractUid")
	List<CustomerContract> findAllByContractUidWithCustomer(Long contractUid);
}
