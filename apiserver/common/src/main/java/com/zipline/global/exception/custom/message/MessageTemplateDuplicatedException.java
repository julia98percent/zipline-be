package com.zipline.global.exception.custom.message;

import com.zipline.global.exception.BaseException;
import org.springframework.http.HttpStatus;


public class MessageTemplateDuplicatedException extends BaseException {
  public MessageTemplateDuplicatedException(String message) {
    super(message, HttpStatus.CONFLICT);
  }
}