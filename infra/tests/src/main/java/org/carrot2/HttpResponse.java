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
package org.carrot2;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.http.Header;

public class HttpResponse {

  private int statusCode;
  private Header[] headers;
  private byte[] responseBody;
  private String statusPhrase;

  public HttpResponse(
      int statusCode, String statusPhrase, Header[] allHeaders, byte[] responseBody) {
    this.statusCode = statusCode;
    this.statusPhrase = statusPhrase;
    this.headers = allHeaders;
    this.responseBody = responseBody;
  }

  public String header(String headerName) {
    for (Header h : headers) {
      if (headerName.equals(h.getName())) {
        return h.getValue();
      }
    }
    throw new RuntimeException("No header named: " + headerName);
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String bodyAsUtf8() {
    if (responseBody == null) {
      throw new RuntimeException("Empty response body.");
    }
    return new String(responseBody, StandardCharsets.UTF_8);
  }

  public byte[] body() {
    if (responseBody == null) {
      throw new RuntimeException("Empty response body.");
    }
    return responseBody;
  }

  public URI locationHeader() {
    return URI.create(header("Location"));
  }

  public HttpResponse assertStatus(int responseCode) {
    if (statusCode != responseCode) {
      throw new AssertionError(
          String.format(
              Locale.ROOT,
              "Expected status code: %s but was: %s (%s); body: %s",
              responseCode,
              statusCode,
              statusPhrase,
              bodyAsUtf8()));
    }
    return this;
  }

  public HttpResponse assertHeader(String name, String value) {
    if (!Objects.equals(header(name), value)) {
      throw new AssertionError(
          String.format(
              Locale.ROOT,
              "Expected header %s with value %s, but was: %s",
              name,
              value,
              header(name)));
    }
    return this;
  }

  public Stream<Map.Entry<String, String>> headers() {
    return Arrays.stream(this.headers)
        .map(header -> Map.entry(header.getName(), header.getValue()));
  }
}
