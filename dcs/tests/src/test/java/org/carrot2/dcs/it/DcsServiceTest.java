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
package org.carrot2.dcs.it;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.carrot2.HttpRequest;
import org.carrot2.dcs.model.ClusterResponse;
import org.carrot2.dcs.model.ErrorResponse;
import org.carrot2.dcs.model.ErrorResponseType;
import org.junit.Test;

public class DcsServiceTest extends AbstractDcsTest {
  @Test
  public void verifyListDefaults() throws IOException {
    String body =
        HttpRequest.builder()
            .sendGet(dcs().getAddress().resolve("/service/list?indent"))
            .assertStatus(HttpServletResponse.SC_OK)
            .bodyAsUtf8();

    Assertions.assertThat(body)
        .isEqualToIgnoringWhitespace(resourceString("listDefaults.response.json"));

    Assertions.assertThat(
            HttpRequest.builder()
                .sendGet(dcs().getAddress())
                .assertStatus(HttpServletResponse.SC_OK)
                .bodyAsUtf8())
        .contains("_welcome_file_test_marker_");
  }

  @Test
  public void testMultivaluedFields() throws IOException {
    String body =
        HttpRequest.builder()
            .bodyAsUtf8(resourceString("multivalued-fields.request.json"))
            .sendPost(dcs().getAddress().resolve("/service/cluster?indent"))
            .assertStatus(HttpServletResponse.SC_OK)
            .bodyAsUtf8();

    System.out.println(body);
    Assertions.assertThat(parseJsonTo(body, ClusterResponse.class)).isNotNull();
  }

  @Test
  public void errorResponseHasBody() throws IOException {
    String body =
        HttpRequest.builder()
            .bodyAsUtf8("{ invalid }")
            .sendPost(dcs().getAddress().resolve("/service/cluster?indent"))
            .assertStatus(HttpServletResponse.SC_BAD_REQUEST)
            .bodyAsUtf8();

    ErrorResponse errorResponse = parseJsonTo(body, ErrorResponse.class);
    Assertions.assertThat(errorResponse.type).isEqualTo(ErrorResponseType.BAD_REQUEST);
    Assertions.assertThat(errorResponse.exception).isEqualTo(JsonParseException.class.getName());
  }

  public static <T> T parseJsonTo(String json, Class<T> clazz) throws JsonProcessingException {
    ObjectMapper om = new ObjectMapper();
    return om.readValue(json, clazz);
  }
}
