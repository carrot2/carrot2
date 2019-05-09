package org.carrot2.dcs.servlets;

import com.carrotsearch.hppc.cursors.IntCursor;
import org.carrot2.attrs.AliasMapper;
import org.carrot2.attrs.Attrs;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.clustering.ClusteringAlgorithmProvider;
import org.carrot2.clustering.Document;
import org.carrot2.dcs.client.ClusterRequest;
import org.carrot2.dcs.client.ClusterResponse;
import org.carrot2.language.LanguageComponents;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClusterServlet extends RestEndpoint {
  public static final String PARAM_TEMPLATE = "template";

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
      Map<String, String> fields = this.source.getFields();
      fields.forEach(fieldConsumer);
      fields.clear();
    }
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    dcsContext = DcsContext.load(config.getServletContext());
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: support streaming mode in which the request is not fully deserialized but parsed and processed on the fly?

    try {
      ClusterRequest template = parseTemplate(request);
      ClusterRequest clusteringRequest = parseRequest(request);

      ClusteringAlgorithm algorithm = parseAlgorithm(template, clusteringRequest);

      // Get language components for the designated language.
      LanguageComponents language = getLanguage(template, clusteringRequest);

      // Write the response.
      List<Cluster<DocumentRef>> clusters = runClustering(clusteringRequest, algorithm, language);
      super.writeJsonResponse(response, shouldIndent(request), new ClusterResponse(adapt(clusters)));
    } catch (TerminateRequestException e) {
      e.handle(response);
    }
  }

  private ClusteringAlgorithm parseAlgorithm(ClusterRequest template, ClusterRequest clusteringRequest) throws TerminateRequestException {
    String algorithmName = firstNotNull(clusteringRequest.algorithm, template.algorithm);
    if (algorithmName == null) {
      throw new TerminateRequestException(HttpServletResponse.SC_BAD_REQUEST,
          "Algorithm must not be empty.");
    }
    ClusteringAlgorithmProvider supplier = dcsContext.algorithmSuppliers.get(algorithmName);
    if (supplier == null) {
      throw new TerminateRequestException(HttpServletResponse.SC_BAD_REQUEST,
          "Algorithm not available: " + algorithmName);
    }

    ClusteringAlgorithm algorithm = supplier.get();

    if (template.parameters != null) {
      algorithm.accept(
          new Attrs.FromMapVisitor(template.parameters, AliasMapper.SPI_DEFAULTS::fromName));
    }

    if (clusteringRequest.parameters != null) {
      algorithm.accept(
          new Attrs.FromMapVisitor(clusteringRequest.parameters, AliasMapper.SPI_DEFAULTS::fromName));
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

  private ClusterRequest parseTemplate(HttpServletRequest request) throws TerminateRequestException {
    String templateName = request.getParameter(PARAM_TEMPLATE);
    if (templateName == null) {
      return templateDefault;
    }

    ClusterRequest template = dcsContext.templates.get(templateName);
    if (template == null) {
      throw new TerminateRequestException(HttpServletResponse.SC_BAD_REQUEST,
          "Template not available: " + templateName);
    }
    return template;
  }

  private LanguageComponents getLanguage(ClusterRequest template, ClusterRequest clusteringRequest) throws TerminateRequestException {
    if (clusteringRequest.language == null) {
      clusteringRequest.language = template.language;
    }

    String requestedLanguage = clusteringRequest.language;
    if (requestedLanguage == null) {
      throw new TerminateRequestException(HttpServletResponse.SC_BAD_REQUEST,
          "Clustering language must not be empty.");
    }

    LanguageComponents language = dcsContext.getLanguage(requestedLanguage);
    if (language == null) {
      throw new TerminateRequestException(HttpServletResponse.SC_BAD_REQUEST,
          "Language not available: " + clusteringRequest.language);
    }
    return language;
  }

  private List<Cluster<Integer>> adapt(List<Cluster<DocumentRef>> clusters) {
    return clusters.stream().map(c -> {
      Cluster<Integer> clone = new Cluster<>();
      clone.setScore(c.getScore());
      c.getLabels().forEach(clone::addLabel);
      c.getDocuments().forEach(doc -> clone.addDocument(doc.ord));
      adapt(c.getClusters()).forEach(clone::addCluster);
      return clone;
    }).collect(Collectors.toList());
  }

  private List<Cluster<DocumentRef>> runClustering(ClusterRequest clusteringRequest,
                                                   ClusteringAlgorithm algorithm,
                                                   LanguageComponents language) {
    IntCursor c = new IntCursor();
    Stream<DocumentRef> stream = clusteringRequest.documents.stream()
        .sequential()
        .map(doc -> new DocumentRef(doc, c.value++));

    return algorithm.cluster(stream, language);
  }

  private ClusterRequest parseRequest(HttpServletRequest request) throws TerminateRequestException {
    try {
      return dcsContext.om.readerFor(ClusterRequest.class)
          .readValue(new BufferedInputStream(request.getInputStream()));
    } catch (IOException e) {
      throw new TerminateRequestException(HttpServletResponse.SC_BAD_REQUEST, "Could not parse request body.", e);
    }
  }
}
