package com.zipline.survey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zipline.survey.entity.SurveyAnswer;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {

	@Query("SELECT sa FROM SurveyAnswer sa JOIN FETCH sa.question q LEFT JOIN FETCH q.choices WHERE sa.surveyResponse.uid = :surveyResponseUid")
	List<SurveyAnswer> findByWithQuestionsAndChoicesSurveyResponseUid(Long surveyResponseUid);
}