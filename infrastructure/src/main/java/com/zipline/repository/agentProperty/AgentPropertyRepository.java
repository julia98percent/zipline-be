package com.zipline.repository.agentProperty;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zipline.entity.agentProperty.AgentProperty;

@Repository
public interface AgentPropertyRepository extends JpaRepository<AgentProperty, Long> {

	Optional<AgentProperty> findByUidAndUserUidAndDeletedAtIsNull(Long propertyUid, Long userUid);

	@Query("SELECT a FROM AgentProperty a WHERE a.user.uid = :userUID AND a.deletedAt IS NULL ORDER BY a.uid DESC")
	Page<AgentProperty> findByUserUidAndDeletedAtIsNull(Long userUID, Pageable pageable);

	Page<AgentProperty> findByCustomerUidAndUserUidAndDeletedAtIsNullOrderByCreatedAtDesc(Long customerUid,
		Long userUid, Pageable pageable);

	@Query("SELECT a FROM AgentProperty a WHERE a.customer.uid = :customerUid AND a.address = :address AND a.detailAddress = :detailAddress AND a.legalDistrictCode = :legalDistrictCode AND a.deletedAt IS NULL")
	Optional<AgentProperty> findDuplicateProperty(Long customerUid, String address, String detailAddress,
		String legalDistrictCode);

}
