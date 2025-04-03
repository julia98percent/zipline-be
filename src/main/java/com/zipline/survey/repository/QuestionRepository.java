package com.zipline.survey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.survey.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

	List<Question> findBySurveyUid(Long surveyUID);
}
