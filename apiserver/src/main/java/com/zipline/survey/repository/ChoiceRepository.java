package com.zipline.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.survey.entity.Choice;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
}
