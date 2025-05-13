package com.zipline.repository.counsel;

import com.zipline.entity.counsel.Counsel;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CounselRepository extends JpaRepository<Counsel, Long>, QCounselRepository {
	Optional<Counsel> findByUidAndUserUidAndDeletedAtIsNull(Long uid, Long userUid);

	List<Counsel> findByCustomerUidAndUserUidAndDeletedAtIsNullOrderByCreatedAtDesc(Long customerUid, Long userUid);

	Page<Counsel> findByCustomerUidAndUserUidAndDeletedAtIsNullOrderByCreatedAtDesc(Long customerUid, Long userUid, Pageable pageable);

	@Query("SELECT c FROM Counsel c JOIN FETCH c.customer WHERE c.user.uid = :userUid AND c.agentProperty.uid = :propertyUid AND c.deletedAt IS NULL ORDER BY c.counselDate desc")
	Page<Counsel> findByUserUidAndAgentPropertyUidAndDeletedAtIsNull(Long userUid, Long propertyUid, Pageable pageable);
}