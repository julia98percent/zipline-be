package com.zipline.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.user.PasswordQuestion;
import com.zipline.entity.user.PasswordQuestionAnswer;
import com.zipline.entity.user.User;

public interface PasswordQuestionAnswerRepository extends JpaRepository<PasswordQuestionAnswer, Long> {
	Optional<PasswordQuestionAnswer> findByUserAndPasswordQuestion(User user, PasswordQuestion passwordQuestion);
}
