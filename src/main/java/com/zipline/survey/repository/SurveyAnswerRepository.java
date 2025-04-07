package com.zipline.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.survey.entity.SurveyAnswer;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {
}