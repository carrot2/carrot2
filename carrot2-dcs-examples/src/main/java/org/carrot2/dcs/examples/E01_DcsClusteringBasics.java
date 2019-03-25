package org.carrot2.dcs.examples;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.carrot2.dcs.client.ClusterRequest;
import org.carrot2.dcs.client.ClusterResponse;
import org.carrot2.dcs.client.ListResponse;
import org.carrot2.examples.ExamplesCommon;
import org.carrot2.examples.ExamplesData;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

public class E01_DcsClusteringBasics implements Runnable, Closeable {
  private final CloseableHttpClient httpClient;
  private final URI dcsService;
  private final ObjectMapper om;

  public E01_DcsClusteringBasics(URI dcsService) {
    this.httpClient = HttpClientBuilder.create()
        .disableAutomaticRetries()
        .disableContentCompression()
        .disableRedirectHandling()
        .setDefaultRequestConfig(RequestConfig.custom()
            .setMaxRedirects(0)
            .setConnectionRequestTimeout(2000)
            .setConnectTimeout(2000)
            .build())
        .build();

    this.dcsService = Objects.requireNonNull(dcsService);
    this.om = new ObjectMapper();
    this.om.enable(SerializationFeature.INDENT_OUTPUT);
  }

  public void listTemplates() throws IOException {
    HttpUriRequest request = RequestBuilder.get(dcsService.resolve("list")).build();
    try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
      expect(httpResponse, HttpStatus.SC_OK);

      ListResponse response = om.readValue(httpResponse.getEntity().getContent(), ListResponse.class);
      System.out.println("Available algorithms: " + response.algorithms);
      System.out.println("Available languages: " + response.languages);
      System.out.println("Available templates: " + response.templates);
    }
  }

  public void clusterDocuments() throws IOException {
    ClusterRequest request = new ClusterRequest();
    request.algorithm = "Lingo";
    request.language = "English";
    request.documents = ExamplesData.documentStream()
        .map(exDoc -> {
          ClusterRequest.Document doc = new ClusterRequest.Document();
          exDoc.visitFields((fld, value) -> doc.setField(fld, value));
          return doc;
        })
        .collect(Collectors.toList());

    HttpUriRequest httpRequest = RequestBuilder.post(dcsService.resolve("cluster"))
        .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .setEntity(new ByteArrayEntity(om.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)))
        .build();

    try (CloseableHttpResponse httpResponse = httpClient.execute(httpRequest)) {
      expect(httpResponse, HttpStatus.SC_OK);

      ClusterResponse response = om.readValue(httpResponse.getEntity().getContent(), ClusterResponse.class);
      System.out.println("Clusters returned:");
      ExamplesCommon.printClusters(response.clusters);
    }
  }

  private void expect(CloseableHttpResponse httpResponse, int code) throws IOException {
    if (httpResponse.getStatusLine().getStatusCode() != code) {
      throw new IOException("Unexpected response: " + httpResponse.getStatusLine());
    }
  }

  @Override
  public void run() {
    try {
      listTemplates();
      clusterDocuments();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void close() throws IOException {
    httpClient.close();
  }

  public static void main(String [] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Provide DCS service URI, typically: http://localhost:8080/service/");
      System.exit(-1);
    }

    System.out.println("Connecting to: " +  args[0]);
    try (E01_DcsClusteringBasics ex = new E01_DcsClusteringBasics(URI.create(args[0]))) {
      ex.run();
    }
  }
}
