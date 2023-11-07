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

import com.carrotsearch.hppc.cursors.IntCursor;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.carrot2.attrs.AliasMapper;
import org.carrot2.attrs.Attrs;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.clustering.ClusteringAlgorithmProvider;
import org.carrot2.clustering.Document;
import org.carrot2.dcs.model.ClusterRequest;
import org.carrot2.dcs.model.ClusterResponse;
import org.carrot2.dcs.model.ClusterServletParameters;
import org.carrot2.dcs.model.ErrorResponseType;
import org.carrot2.dcs.model.ServiceInfo;
import org.carrot2.language.LanguageComponents;

@SuppressWarnings("serial")
public class ClusterServlet extends RestEndpoint {
  public static final String PARAM_SERVICE_INFO = "serviceInfo";

  private DcsContext dcsContext;
  private ClusterRequest templateDefault = new ClusterRequest();

  private static class DocumentRef implements Document {
    int ord;
    ClusterRequest.Document source;

    public DocumentRef(ClusterRequest.Document doc, int ord) {
      this.source = doc;
      this.ord = ord;
    }

    @Override
    public void visitFields(BiConsumer<String, String> fieldConsumer) {
      // Visit all fields of the document and clear
      // the reference early, we only need the ordinal.
      this.source.visitFields(fieldConsumer);
      this.source = null;
    }
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    dcsContext = DcsContext.load(config.getServletContext());
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    // TODO: support streaming mode in which the request is not fully deserialized but parsed and
    // processed on the fly?

    try {
      ServiceInfo serviceInfo = new ServiceInfo();

      Stopwatch swRequest = new Stopwatch();
      ClusterRequest template = parseTemplate(request);
      ClusterRequest clusteringRequest = parseRequest(request);

      ClusteringAlgorithm algorithm = parseAlgorithm(template, clusteringRequest);

      // Get language components for the designated language.
      LanguageComponents language = getLanguage(template, clusteringRequest);

      // Run the clustering.
      Stopwatch swClustering = new Stopwatch();
      List<Cluster<DocumentRef>> clusters = runClustering(clusteringRequest, algorithm, language);
      serviceInfo.clusteringTimeMillis = swClustering.elapsedMillis();

      ClusterResponse clusterResponse = new ClusterResponse(adapt(clusters));
      serviceInfo.requestHandlingTimeMillis = swRequest.elapsedMillis();

      if (isEnabled(request, PARAM_SERVICE_INFO)) {
        clusterResponse.serviceInfo = serviceInfo;
      }

      writeJsonResponse(response, shouldIndent(request), clusterResponse);
    } catch (Exception e) {
      handleException(request, response, e);
    }
  }

  private ClusteringAlgorithm parseAlgorithm(
      ClusterRequest template, ClusterRequest clusteringRequest) throws TerminateRequestException {
    String algorithmName = firstNotNull(clusteringRequest.algorithm, template.algorithm);
    if (algorithmName == null) {
      throw new TerminateRequestException(
          ErrorResponseType.BAD_REQUEST, "Algorithm must not be empty.");
    }
    ClusteringAlgorithmProvider supplier = dcsContext.algorithmSuppliers.get(algorithmName);
    if (supplier == null) {
      throw new TerminateRequestException(
          ErrorResponseType.BAD_REQUEST, "Algorithm not available: " + algorithmName);
    }

    Function<String, Object> classFromName = AliasMapper.SPI_DEFAULTS::fromName;
    ClusteringAlgorithm algorithm = supplier.get();

    try {
      if (template.parameters != null) {
        Attrs.populate(algorithm, template.parameters, classFromName);
      }
      if (clusteringRequest.parameters != null) {
        Attrs.populate(algorithm, clusteringRequest.parameters, classFromName);
      }
    } catch (IllegalArgumentException e) {
      throw new TerminateRequestException(ErrorResponseType.BAD_REQUEST, e.getMessage(), e);
    }

    return algorithm;
  }

  private static String firstNotNull(String first, String... other) {
    if (first != null) return first;
    for (String v : other) {
      if (v != null) {
        return v;
      }
    }
    return null;
  }

  private ClusterRequest parseTemplate(HttpServletRequest request)
      throws TerminateRequestException {
    String templateName = request.getParameter(ClusterServletParameters.PARAM_TEMPLATE);
    if (templateName == null) {
      return templateDefault;
    }

    ClusterRequest template = dcsContext.templates.get(templateName);
    if (template == null) {
      throw new TerminateRequestException(
          ErrorResponseType.BAD_REQUEST, "Template not available: " + templateName);
    }
    return template;
  }

  private LanguageComponents getLanguage(ClusterRequest template, ClusterRequest clusteringRequest)
      throws TerminateRequestException {
    if (clusteringRequest.language == null) {
      clusteringRequest.language = template.language;
    }

    String requestedLanguage = clusteringRequest.language;
    if (requestedLanguage == null) {
      throw new TerminateRequestException(
          ErrorResponseType.BAD_REQUEST, "Clustering language must not be empty.");
    }

    LanguageComponents language = dcsContext.getLanguage(requestedLanguage);
    if (language == null) {
      throw new TerminateRequestException(
          ErrorResponseType.BAD_REQUEST, "Language not available: " + clusteringRequest.language);
    }
    return language;
  }

  private List<Cluster<Integer>> adapt(List<Cluster<DocumentRef>> clusters) {
    return clusters.stream()
        .map(
            c -> {
              Cluster<Integer> clone = new Cluster<>();
              clone.setScore(c.getScore());
              c.getLabels().forEach(clone::addLabel);
              c.getDocuments().forEach(doc -> clone.addDocument(doc.ord));
              adapt(c.getClusters()).forEach(clone::addCluster);
              return clone;
            })
        .collect(Collectors.toList());
  }

  private List<Cluster<DocumentRef>> runClustering(
      ClusterRequest clusteringRequest,
      ClusteringAlgorithm algorithm,
      LanguageComponents language) {
    IntCursor c = new IntCursor();
    Stream<DocumentRef> stream =
        clusteringRequest.documents.stream()
            .sequential()
            .map(doc -> new DocumentRef(doc, c.value++));

    return algorithm.cluster(stream, language);
  }

  private ClusterRequest parseRequest(HttpServletRequest request) throws TerminateRequestException {
    try {
      return dcsContext
          .om
          .readerFor(ClusterRequest.class)
          .readValue(new BufferedInputStream(request.getInputStream()));
    } catch (IOException e) {
      throw new TerminateRequestException(
          ErrorResponseType.BAD_REQUEST, "Could not parse request body.", e);
    }
  }
}
