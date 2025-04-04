package com.zipline.survey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zipline.survey.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	@Query("SELECT q FROM Question q JOIN FETCH q.choices WHERE q.survey.uid = :surveyUID ")
	List<Question> findAllBySurveyUidWithChoices(Long surveyUID);
}
