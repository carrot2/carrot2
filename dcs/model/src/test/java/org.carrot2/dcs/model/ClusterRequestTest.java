/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.dcs.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Test;

public class ClusterRequestTest {
  @Test
  public void testStructure() throws JsonProcessingException {
    ClusterRequest req = new ClusterRequest();

    req.language = "English";
    req.algorithm = "Lingo";

    req.parameters = new LinkedHashMap<>();
    req.parameters.put("foo", "bar");
    req.parameters.put("bar", new LinkedList<>(Arrays.asList("baz")));
    req.parameters.put("baz", new ConcurrentHashMap());

    ClusterRequest.Document doc;
    doc = new ClusterRequest.Document();
    doc.setField("title", "baz");
    doc.setField("snippet", "bar");
    req.documents.add(doc);

    doc = new ClusterRequest.Document();
    doc.setField("title", "foo");
    req.documents.add(doc);

    ObjectMapper om = new ObjectMapper();
    String s = om.writerWithDefaultPrettyPrinter().writeValueAsString(req);

    System.out.println(s);
  }
}
