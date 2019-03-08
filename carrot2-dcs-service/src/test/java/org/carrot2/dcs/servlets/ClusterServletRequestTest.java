package org.carrot2.dcs.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class ClusterServletRequestTest {
  @Test
  public void testStructure() throws JsonProcessingException {
    ClusterServletRequest req = new ClusterServletRequest();
    ClusterServletRequest.Document doc;

    doc = new ClusterServletRequest.Document();
    doc.setField("title", "baz");
    doc.setField("snippet", "bar");
    req.documents.add(doc);

    doc = new ClusterServletRequest.Document();
    doc.setField("title", "foo");
    req.documents.add(doc);

    String s = new ObjectMapper().writeValueAsString(req);
    System.out.println(s);
  }
}
