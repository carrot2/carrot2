package org.carrot2.dcs.servlets;

public class TerminateRequestException extends Exception {
  public final int code;

  TerminateRequestException(int httpCode, String message, Throwable cause) {
    super(message, cause);
    this.code = httpCode;
  }

  TerminateRequestException(int httpCode, String message) {
    super(message);
    this.code = httpCode;
  }
}
