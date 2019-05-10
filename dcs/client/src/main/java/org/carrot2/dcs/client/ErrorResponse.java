package org.carrot2.dcs.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.PrintWriter;
import java.io.StringWriter;

@JsonPropertyOrder({
    "class",
    "message",
    "stacktrace"
})
public class ErrorResponse {
  @JsonProperty(value = "class")
  public String clazz;

  @JsonProperty
  public String message;

  @JsonProperty
  public String stacktrace;

  public ErrorResponse() {
  }

  public ErrorResponse(Throwable exception) {
    this.clazz = exception.getClass().getName();
    this.message = exception.getMessage();

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    exception.printStackTrace(pw);
    pw.flush();
    this.stacktrace = sw.toString();
  }
}
