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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.junit.Test;

public class ListResponseTest extends TestBase {
  @Test
  public void testStructure() throws JsonProcessingException {
    Map<String, List<String>> algorithms = new LinkedHashMap<>();
    algorithms.put("algorithm2", List.of("lang3", "lang4"));
    algorithms.put("algorithm1", List.of("lang1", "lang2"));

    ClusterRequest template1 = new ClusterRequest();
    template1.language = "lang1";
    template1.algorithm = "algorithm1";

    ClusterRequest template2 = new ClusterRequest();
    template2.language = null; // No language in the template.
    template2.algorithm = "algorithm1";
    template2.parameters = Map.of("param1", "should not be exposed");

    Map<String, ClusterRequest> templates = new LinkedHashMap<>();
    templates.put("template1", template1);
    templates.put("template2", template2);

    ListResponse response =
        new ListResponse(algorithms, ListResponse.filterSensitiveDataFromTemplates(templates));

    ObjectMapper om = new ObjectMapper();
    String actual = om.writerWithDefaultPrettyPrinter().writeValueAsString(response);

    Assertions.assertThat(actual).isEqualToIgnoringWhitespace(resourceString("ListResponse.json"));
  }
}
