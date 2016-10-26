
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.microsoft.v5;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.carrot2.shaded.guava.common.io.ByteStreams;
import org.carrot2.source.microsoft.v5.ErrorResponse.Error;
import org.carrot2.util.tests.CarrotTestCase;
import org.fest.assertions.Assertions;
import org.junit.Test;

/**
 * Tests Microsoft Bing document source.
 */
public class BingResponseTest extends CarrotTestCase {
  @Test
  public void testInvalidKey() throws Exception {
    BingResponse response = parse("bing.v5.invalidkey.json");

    Assertions.assertThat(response).isInstanceOf(UnstructuredResponse.class);
    UnstructuredResponse r = (UnstructuredResponse) response;

    Assertions.assertThat(r.statusCode).isEqualTo(401);
    Assertions.assertThat(r.message).contains("invalid subscription key");
  }

  @Test
  public void testError() throws Exception {
    BingResponse response = parse("bing.v5.error.json");

    Assertions.assertThat(response).isInstanceOf(ErrorResponse.class);
    ErrorResponse r = (ErrorResponse) response;

    Error error = r.errors.get(0);
    Assertions.assertThat(error.code).isEqualTo("RequestParameterMissing");
  }

  @Test
  public void testValidResponse() throws Exception {
    SearchResponse response = (SearchResponse) parse("bing.v5.response.json");

    Assertions.assertThat(response.news.value).hasSize(5);
    Assertions.assertThat(response.images.value).hasSize(18);
    Assertions.assertThat(response.webPages.value).hasSize(9);
    
    SearchResponse.WebPages.Result result = response.webPages.value.get(0);
    assertNotNull(result.name);
    assertNotNull(result.snippet);
    assertNotNull(result.displayUrl);
  }

  private BingResponse parse(String resource) throws IOException {
    byte[] json = ByteStreams.toByteArray(getClass().getResourceAsStream(resource));
    return BingResponse.parse(new ByteArrayInputStream(json));
  }
}
