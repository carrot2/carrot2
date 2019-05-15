package org.carrot2.dcs.client;

import java.net.HttpURLConnection;

public enum ErrorResponseType {
  BAD_REQUEST(HttpURLConnection.HTTP_BAD_REQUEST),
  UNHANDLED_ERROR(HttpURLConnection.HTTP_INTERNAL_ERROR);

  public final int httpStatusCode;

  ErrorResponseType(int code) {
    this.httpStatusCode = code;
  }
}
