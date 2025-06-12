package com.zipline.repository.survey;

import com.zipline.entity.survey.SurveyResponse;
import com.zipline.entity.user.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

  @Query("SELECT sr FROM SurveyResponse sr INNER JOIN FETCH sr.survey s INNER JOIN FETCH s.user WHERE sr.uid =:surveyResponseUid")
  Optional<SurveyResponse> findSurveyResponseWithSurveyAndUserById(Long surveyResponseUid);

  @Query("SELECT DISTINCT s.user FROM SurveyResponse sr INNER JOIN sr.survey s INNER JOIN s.user WHERE s.ulid = :surveyUlid")
  Optional<User> findUserBySurveyUlid(String surveyUlid);

  @Query("SELECT sr FROM SurveyResponse sr WHERE sr.survey.user.uid =:userUid ORDER BY sr.createdAt DESC")
  Page<SurveyResponse> findBySurveyUserUid(Long userUid, Pageable pageable);
}