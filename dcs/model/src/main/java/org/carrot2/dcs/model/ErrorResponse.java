/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.dcs.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

@JsonPropertyOrder({"type", "message", "exception", "stacktrace"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  @JsonProperty public ErrorResponseType type;

  @JsonProperty public String message;

  @JsonProperty public String exception;

  @JsonProperty public String stacktrace;

  public ErrorResponse() {}

  public ErrorResponse(ErrorResponseType type, String message, Throwable exception) {
    this.type = Objects.requireNonNull(type);
    this.message = message;

    if (exception != null) {
      this.exception = exception.getClass().getName();

      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      exception.printStackTrace(pw);
      pw.flush();
      this.stacktrace = sw.toString();
    }
  }

  public ErrorResponse(ErrorResponseType type, Throwable exception) {
    this(type, exception != null ? exception.getMessage() : null, exception);
  }

  public ErrorResponse(ErrorResponseType type) {
    this(type, null, null);
  }
}
