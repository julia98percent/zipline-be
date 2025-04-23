package com.zipline.global.exception.custom.message;


import com.zipline.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MessageCommonException extends BaseException {

  public MessageCommonException(String message, HttpStatus status) {
    super(message, status);
  }
}