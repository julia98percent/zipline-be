package com.zipline.repository.survey;

import java.util.Optional;

import com.zipline.entity.enums.SurveyStatus;
import com.zipline.entity.survey.Survey;
import com.zipline.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
	@Query("SELECT s FROM Survey s WHERE s.uid = :surveyUID AND s.status = :status")
	Optional<Survey> findByUidAndStatus(Long surveyUID, SurveyStatus status);
	
	Optional<Survey> findFirstByUserOrderByCreatedAtDesc(User user);
}
