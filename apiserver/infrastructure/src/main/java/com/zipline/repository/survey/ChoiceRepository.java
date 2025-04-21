package com.zipline.repository.survey;

import com.zipline.entity.survey.Choice;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChoiceRepository extends JpaRepository<Choice, Long> {
}
