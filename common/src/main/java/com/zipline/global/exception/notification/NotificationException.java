package com.zipline.global.exception.notification;

import com.zipline.global.exception.BaseException;
import com.zipline.global.exception.notification.errorcode.NotificationErrorCode;

public class NotificationException extends BaseException {

  public NotificationException(NotificationErrorCode errorCode) {
    super(errorCode);
  }
}