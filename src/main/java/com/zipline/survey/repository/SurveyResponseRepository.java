package com.zipline.survey.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zipline.survey.entity.SurveyResponse;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

	@Query("SELECT sr FROM SurveyResponse sr INNER JOIN FETCH sr.survey s INNER JOIN FETCH s.user WHERE sr.uid =:surveyResponseUid")
	Optional<SurveyResponse> findSurveyResponseWithSurveyAndUserById(Long surveyResponseUid);

	@Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.user.uid =:userUid ORDER BY sr.createdAt DESC")
	Page<SurveyResponse> findBySurveyUserUid(Long userUid, Pageable pageable);
}