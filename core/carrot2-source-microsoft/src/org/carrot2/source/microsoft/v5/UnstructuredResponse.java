package org.carrot2.source.microsoft.v5;

import com.fasterxml.jackson.annotation.JsonProperty;

class UnstructuredResponse extends BingResponse {
  @JsonProperty(required = true)
  public int statusCode;

  @JsonProperty
  public String message;
}