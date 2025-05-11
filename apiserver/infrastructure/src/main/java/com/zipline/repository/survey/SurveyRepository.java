package com.zipline.repository.survey;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zipline.entity.survey.Survey;
import com.zipline.entity.user.User;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
	@Query("SELECT s FROM Survey s WHERE s.ulid = :surveyUlid AND s.deletedAt IS NULL")
	Optional<Survey> findByUlidAndDeleteAtIsNull(String surveyUlid);

	@Query("SELECT s FROM Survey s WHERE s.user.uid = :userUid AND s.deletedAt IS NULL")
	Optional<Survey> findByUserUidAndDeletedAtIsNull(Long userUid);

	Optional<Survey> findFirstByUserOrderByCreatedAtDesc(User user);

	int countByUserUidAndCreatedAtAfter(Long userId, LocalDateTime oneMonthAgo);

}
