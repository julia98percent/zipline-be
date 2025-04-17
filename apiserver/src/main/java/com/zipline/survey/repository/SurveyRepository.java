package com.zipline.survey.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zipline.entity.User;
import com.zipline.survey.entity.Survey;
import com.zipline.survey.entity.enums.SurveyStatus;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
	@Query("SELECT s FROM Survey s WHERE s.uid = :surveyUID AND s.status = :status")
	Optional<Survey> findByUidAndStatus(Long surveyUID, SurveyStatus status);
	
	Optional<Survey> findFirstByUserOrderByCreatedAtDesc(User user);
}
