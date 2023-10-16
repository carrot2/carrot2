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
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.carrot2.clustering.Cluster;
import org.junit.Test;

public class ClusterResponseTest extends TestBase {
  @Test
  public void testStructure() throws JsonProcessingException {
    List<Cluster<Integer>> clusters = new ArrayList<>();

    Cluster<Integer> c = new Cluster<>();
    c.addLabel("foo");
    c.addLabel("bar");
    c.setScore(36.6);
    c.addDocument(3);
    c.addDocument(2);
    c.addDocument(1);
    clusters.add(c);

    c = new Cluster<>();
    c.addLabel("baz");
    c.addDocument(4);
    clusters.add(c);

    ClusterResponse response = new ClusterResponse(clusters);
    ObjectMapper om = new ObjectMapper();
    String actual = om.writerWithDefaultPrettyPrinter().writeValueAsString(response);

    Assertions.assertThat(actual)
        .isEqualToIgnoringWhitespace(resourceString("ClusterResponse.json"));
  }
}
