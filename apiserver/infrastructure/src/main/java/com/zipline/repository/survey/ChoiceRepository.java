package com.zipline.repository.survey;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.survey.Choice;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
}
