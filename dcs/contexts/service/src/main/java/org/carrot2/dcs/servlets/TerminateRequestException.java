package org.carrot2.dcs.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TerminateRequestException extends Exception {
  private static Logger log = LoggerFactory.getLogger(TerminateRequestException.class);

  private final int code;

  TerminateRequestException(int httpCode, String message, Throwable cause) {
    super(message, cause);
    this.code = httpCode;
  }

  TerminateRequestException(int httpCode, String message) {
    super(message);
    this.code = httpCode;
  }

  public void handle(HttpServletResponse response) throws IOException {
    if (response.isCommitted()) {
      log.warn("Response already committed. Ignoring: {}", getMessage());
    } else {
      response.sendError(code, getMessage());
    }
  }
}
