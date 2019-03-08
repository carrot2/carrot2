package org.carrot2.dcs.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class ClusterRequestTest {
  @Test
  public void testStructure() throws JsonProcessingException {
    ClusterRequest req = new ClusterRequest();
    ClusterRequest.Document doc;

    doc = new ClusterRequest.Document();
    doc.setField("title", "baz");
    doc.setField("snippet", "bar");
    req.documents.add(doc);

    doc = new ClusterRequest.Document();
    doc.setField("title", "foo");
    req.documents.add(doc);

    String s = new ObjectMapper().writeValueAsString(req);
    System.out.println(s);
  }
}
