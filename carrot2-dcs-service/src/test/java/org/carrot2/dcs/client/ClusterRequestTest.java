package org.carrot2.dcs.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class ClusterRequestTest {
  @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS)
  public static class Inner {
    @JsonProperty
    public String foo = "baz";
  }

  @Test
  public void testStructure() throws JsonProcessingException {
    ClusterRequest req = new ClusterRequest();

    req.parameters = new LinkedHashMap<>();
    req.parameters.put("foo", "bar");
    req.parameters.put("bar", new LinkedList<>(Arrays.asList("baz")));
    req.parameters.put("baz", new ConcurrentHashMap());
    req.parameters.put("baf", new Inner());

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
