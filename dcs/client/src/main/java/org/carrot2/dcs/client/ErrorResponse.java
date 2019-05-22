package org.carrot2.dcs.client;

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

  public ErrorResponse(ErrorResponseType type, Throwable exception) {
    this.type = Objects.requireNonNull(type);

    if (exception != null) {
      this.exception = exception.getClass().getName();
      this.message = exception.getMessage();

      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      exception.printStackTrace(pw);
      pw.flush();
      this.stacktrace = sw.toString();
    }
  }
}
