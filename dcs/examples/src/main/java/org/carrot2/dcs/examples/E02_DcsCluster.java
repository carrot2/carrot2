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
package org.carrot2.dcs.examples;

import com.carrotsearch.console.jcommander.Parameters;
import com.carrotsearch.console.launcher.ExitCode;
import com.carrotsearch.console.launcher.ExitCodes;
import com.carrotsearch.console.launcher.Launcher;
import com.carrotsearch.console.launcher.Loggers;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.carrot2.dcs.model.ClusterRequest;
import org.carrot2.dcs.model.ClusterResponse;
import org.carrot2.examples.ExamplesCommon;
import org.carrot2.examples.ExamplesData;

@Parameters(commandNames = "cluster")
public class E02_DcsCluster extends CommandScaffold {
  @Override
  ExitCode run(CloseableHttpClient httpClient, ObjectMapper om) throws IOException {
    ClusterRequest request = new ClusterRequest();
    request.algorithm = "Lingo";
    request.language = "English";
    request.documents =
        ExamplesData.documentStream()
            .map(
                exDoc -> {
                  ClusterRequest.Document doc = new ClusterRequest.Document();
                  exDoc.visitFields((fld, value) -> doc.setField(fld, value));
                  return doc;
                })
            .collect(Collectors.toList());

    HttpUriRequest httpRequest =
        RequestBuilder.post(dcsService.resolve("cluster"))
            .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .setEntity(
                new ByteArrayEntity(
                    om.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)))
            .build();

    try (CloseableHttpResponse httpResponse = httpClient.execute(httpRequest)) {
      expect(httpResponse, HttpStatus.SC_OK);

      ClusterResponse response =
          om.readValue(httpResponse.getEntity().getContent(), ClusterResponse.class);

      Loggers.CONSOLE.info("Clusters returned:");
      ExamplesCommon.printClusters(response.clusters);
    }

    return ExitCodes.SUCCESS;
  }

  public static void main(String[] args) {
    ExitCode exitCode = new Launcher().runCommand(new E02_DcsCluster(), args);
    System.exit(exitCode.processReturnValue());
  }
}
