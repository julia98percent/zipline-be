package com.zipline.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.survey.entity.SurveyResponse;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
}