package org.carrot2.dcs.model;

public interface ErrorResponseHandler {
  ErrorResponse handle(Throwable exception);
}
