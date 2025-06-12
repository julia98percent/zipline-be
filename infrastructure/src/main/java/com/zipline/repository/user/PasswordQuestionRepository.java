package com.zipline.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipline.entity.user.PasswordQuestion;

public interface PasswordQuestionRepository extends JpaRepository<PasswordQuestion, Long> {

}
