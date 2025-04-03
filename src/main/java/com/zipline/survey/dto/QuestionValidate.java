package com.zipline.survey.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;

@Constraint(validatedBy = QuestionValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface QuestionValidate {
	String message() default "질문 데이터가 유효하지 않습니다.";

	Class[] groups() default {};

	Class[] payload() default {};
}
