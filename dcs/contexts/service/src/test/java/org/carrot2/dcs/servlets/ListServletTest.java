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
package org.carrot2.dcs.servlets;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.carrot2.clustering.kmeans.BisectingKMeansClusteringAlgorithm;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.dcs.model.ListResponse;
import org.carrot2.language.LanguageComponents;
import org.junit.Test;

public class ListServletTest extends AbstractServletTest {
  @Test
  public void testGet() throws Exception {
    setupMockTemplates(
        (name) -> resourceStream("template1.json"),
        "01 template2.json",
        "02-template1.json",
        "03 - template3.json");

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    when(response.getWriter()).thenReturn(pw);

    ListServlet servlet = new ListServlet();
    servlet.init(config);
    servlet.doGet(request, response);
    pw.flush();

    ObjectMapper om = new ObjectMapper();
    String content = sw.toString();
    ListResponse response = om.readValue(content, ListResponse.class);

    Assertions.assertThat(response.algorithms.keySet())
        .containsExactly(
            BisectingKMeansClusteringAlgorithm.NAME,
            "Dummy",
            LingoClusteringAlgorithm.NAME,
            STCClusteringAlgorithm.NAME);

    List<String> allLangs = new ArrayList<>(LanguageComponents.loader().load().languages());
    response.algorithms.forEach(
        (algorithm, langs) -> {
          Assertions.assertThat(langs)
              .as("Algorithm: " + algorithm)
              .containsExactlyElementsOf(allLangs);
        });

    Assertions.assertThat(response.templates.keySet().stream())
        .containsExactly("template2", "template1", "template3");

    Assertions.assertThat(content).isEqualToIgnoringNewLines(resourceString("list.response.json"));
  }

  @Test
  public void testTemplateFiltering() throws Exception {
    setupMockTemplates("template1.json", "template-unavailable.json", "template-no-algorithm.json");

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    when(response.getWriter()).thenReturn(pw);

    ListServlet servlet = new ListServlet();
    servlet.init(config);
    servlet.doGet(request, response);
    pw.flush();

    ObjectMapper om = new ObjectMapper();
    Assertions.assertThat(
            om.readValue(sw.toString(), ListResponse.class).templates.keySet().stream())
        .containsOnly("template1", "template-no-algorithm");
  }

  @Test
  public void testAlgorithmFiltering() throws Exception {
    List<String> allowed = List.of(LingoClusteringAlgorithm.NAME, STCClusteringAlgorithm.NAME);

    when(super.context.getInitParameter(DcsContext.PARAM_ALGORITHMS))
        .thenReturn(String.join(", ", allowed));

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    when(response.getWriter()).thenReturn(pw);

    ListServlet servlet = new ListServlet();
    servlet.init(config);
    servlet.doGet(request, response);
    pw.flush();

    ObjectMapper om = new ObjectMapper();
    Assertions.assertThat(om.readValue(sw.toString(), ListResponse.class).algorithms.keySet())
        .hasSameElementsAs(allowed);
  }
}
