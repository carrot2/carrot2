package org.carrot2.dcs.servlets;

import org.carrot2.dcs.client.ErrorResponseType;

public class TerminateRequestException extends Exception {
  public final ErrorResponseType type;

  TerminateRequestException(ErrorResponseType type, String message, Throwable cause) {
    super(message, cause);
    this.type = type;
  }

  TerminateRequestException(ErrorResponseType type, String message) {
    super(message);
    this.type = type;
  }
}
