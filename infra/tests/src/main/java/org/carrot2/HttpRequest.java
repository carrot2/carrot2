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

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpRequest {
  HttpRequest() {}

  public static HttpRequestBuilder builder() {
    return new HttpRequestBuilder();
  }

  public static class HttpRequestBuilder {
    List<KeyValue> queryParams = new ArrayList<>();
    List<KeyValue> headers = new ArrayList<>();
    byte[] body;
    boolean allowCompression = false;

    HttpRequestBuilder() {}

    public HttpResponse sendGet(URI path) throws IOException {
      return sendRequest(path, RequestBuilder.get());
    }

    public HttpResponse sendPost(URI path) throws IOException {
      return sendRequest(path, RequestBuilder.post());
    }

    public HttpResponse sendDelete(URI path) throws IOException {
      return sendRequest(path, RequestBuilder.delete());
    }

    public HttpRequestBuilder queryParam(String key, String value) {
      queryParams.add(new KeyValue(key, value));
      return this;
    }

    public HttpRequestBuilder body(byte[] bodyBytes) {
      if (body != null) {
        throw new RuntimeException("Body already set.");
      }
      this.body = bodyBytes;
      return this;
    }

    public HttpRequestBuilder bodyAsUtf8(String spec) {
      return body(spec.getBytes(StandardCharsets.UTF_8));
    }

    public HttpRequestBuilder header(String key, String value) {
      headers.add(new KeyValue(key, value));
      return this;
    }

    private void checkPath(URI path) {
      if (path.getScheme() == null || !path.getScheme().matches("https?")) {
        throw new RuntimeException("Paths must be absolute: " + path);
      }
    }

    private HttpResponse sendRequest(URI path, RequestBuilder rb) throws IOException {
      checkPath(path);

      for (KeyValue qp : queryParams) {
        rb.addParameter(qp.key, qp.value);
      }

      for (KeyValue qp : headers) {
        rb.addHeader(qp.key, qp.value);
      }

      if (body != null) {
        rb.setEntity(new ByteArrayEntity(body));
        if (rb.getMethod().equals("get")) {
          throw new RuntimeException("GET request with a body?");
        }
      }

      rb.setUri(path);
      HttpUriRequest request = rb.build();

      HttpClientBuilder clientBuilder =
          HttpClientBuilder.create()
              .disableAutomaticRetries()
              .disableRedirectHandling()
              .setDefaultRequestConfig(
                  RequestConfig.custom()
                      .setMaxRedirects(0)
                      .setConnectionRequestTimeout(3000)
                      .setConnectTimeout(3000)
                      .build());

      if (!allowCompression) {
        clientBuilder.disableContentCompression();
      }

      try (CloseableHttpClient httpclient = clientBuilder.build()) {
        try (CloseableHttpResponse response = httpclient.execute(request)) {
          StatusLine statusLine = response.getStatusLine();
          int statusCode = statusLine.getStatusCode();
          String reasonPhrase = statusLine.getReasonPhrase();
          Header[] allHeaders = response.getAllHeaders();

          byte[] responseBody = null;
          HttpEntity entity = response.getEntity();
          if (entity != null) {
            responseBody = EntityUtils.toByteArray(entity);
          }
          return new HttpResponse(statusCode, reasonPhrase, allHeaders, responseBody);
        }
      }
    }

    public HttpRequestBuilder allowCompressedResponse(boolean enable) {
      this.allowCompression = enable;
      return this;
    }
  }

  static class KeyValue {
    final String key;
    final String value;

    KeyValue(String key, String value) {
      this.key = key;
      this.value = value;
    }
  }
}
