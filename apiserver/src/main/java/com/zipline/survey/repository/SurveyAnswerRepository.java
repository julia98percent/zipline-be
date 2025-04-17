package com.zipline.survey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zipline.survey.entity.SurveyAnswer;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {

	@Query("SELECT sa FROM SurveyAnswer sa JOIN FETCH sa.question q LEFT JOIN FETCH q.choices WHERE sa.surveyResponse.uid = :surveyResponseUid")
	List<SurveyAnswer> findByWithQuestionsAndChoicesSurveyResponseUid(Long surveyResponseUid);

	@Query(value = "SELECT * FROM (SELECT sa.*, ROW_NUMBER() OVER (PARTITION BY sa.survey_response_uid ORDER BY sa.uid ASC) AS rn FROM survey_answers sa WHERE sa.survey_response_uid IN (:savedSurveyResponseIds)) AS sa WHERE sa.rn <= 2", nativeQuery = true)
	List<SurveyAnswer> findTop2ByResponseIdIn(List<Long> savedSurveyResponseIds);
}