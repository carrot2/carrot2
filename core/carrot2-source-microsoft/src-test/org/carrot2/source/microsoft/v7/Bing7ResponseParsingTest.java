
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2018, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.microsoft.v7;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.carrot2.shaded.guava.common.io.ByteStreams;
import org.carrot2.util.tests.CarrotTestCase;
import org.fest.assertions.Assertions;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests Microsoft Bing document source.
 */
public class Bing7ResponseParsingTest extends CarrotTestCase {
  @Before
  public void checkKeyAvailable() {
    Assume.assumeTrue(System.getProperty(Bing7DocumentSource.SYSPROP_BING7_API) != null);
  }

  @Test
  public void testInvalidKey() throws Exception {
    BingResponse response = parse("bing.v7.invalidkey.json");

    Assertions.assertThat(response).isInstanceOf(UnstructuredResponse.class);
    UnstructuredResponse r = (UnstructuredResponse) response;

    Assertions.assertThat(r.statusCode).isEqualTo(401);
    Assertions.assertThat(r.message).contains("invalid subscription key");
  }

  @Test
  public void testError() throws Exception {
    BingResponse response = parse("bing.v7.error.json");

    Assertions.assertThat(response).isInstanceOf(ErrorResponse.class);
    ErrorResponse r = (ErrorResponse) response;

    ErrorResponse.Error error = r.errors.get(0);
    Assertions.assertThat(error.code).isEqualTo("RequestParameterMissing");
  }

  @Test
  public void testValidSearchResponse() throws Exception {
    SearchResponse response = (SearchResponse) parse("bing.v7.response.json");

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
