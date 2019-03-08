package org.carrot2.dcs.servlets;

import com.carrotsearch.hppc.cursors.IntCursor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.carrot2.attrs.Attrs;
import org.carrot2.clustering.Cluster;
import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.clustering.ClusteringAlgorithmProvider;
import org.carrot2.clustering.Document;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.language.LanguageComponents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ClusterServlet extends RestEndpoint {
  private static Logger log = LoggerFactory.getLogger(ClusterServlet.class);

  private ObjectMapper om;
  private Map<String, LanguageComponents> languages;

  private static class DocumentRef implements Document {
    int ord;
    ClusterServletRequest.Document source;

    public DocumentRef(ClusterServletRequest.Document doc, int ord) {
      this.source = doc;
      this.ord = ord;
    }

    @Override
    public void visitFields(BiConsumer<String, String> fieldConsumer) {
      this.source.getFields().forEach(fieldConsumer);
      this.source.clear();
    }
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    this.om = new ObjectMapper();
    om.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

    // TODO: Apply custom resource location customizations.
    languages = new HashMap<>();
    LanguageComponents.languages().stream().forEachOrdered(language -> {
      languages.put(language, LanguageComponents.load(language));
    });
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: support streaming mode in which request is not deserialized but parsed and processed
    // on the fly?

    try {
      ClusterServletRequest clusteringRequest = parseRequest(request);

      // TODO: Identify the algorithm to use, pick preconfigured instance.
      Map<String, ClusteringAlgorithmProvider<?>> algorithms =
          StreamSupport.stream(ServiceLoader.load(ClusteringAlgorithmProvider.class).spliterator(), false)
              .collect(Collectors.toMap(e -> e.name(), e -> e));
      ClusteringAlgorithm algorithm = algorithms.get("Dummy").get();

      // TODO: Get language components for the designated language.
      LanguageComponents language = languages.get("English");

      // Clone algorithm instance for processing.
      Map<String, Object> attrs = Attrs.toMap(algorithm);
      // TODO: Apply any attribute customizations based on the request.
      algorithm = Attrs.fromMap(ClusteringAlgorithm.class, attrs);

      // Write the response.
      List<Cluster<DocumentRef>> clusters = runClustering(clusteringRequest, algorithm, language);
      // TODO: we could write the response directly, without wrapping it in an adapter.
      ClusterServletResponse clusteringResponse = new ClusterServletResponse(adapt(clusters));
      super.writeJsonResponse(response, shouldIndent(request), clusteringResponse);
    } catch (TerminateRequestException e) {
      e.handle(response);
    }
  }

  private List<Cluster<Integer>> adapt(List<Cluster<DocumentRef>> clusters) {
    return clusters.stream().map(c -> {
      Cluster<Integer> clone = new Cluster<>();
      clone.setScore(c.getScore());
      c.getLabels().forEach(clone::addLabel);
      c.getDocuments().forEach(doc -> clone.addDocument(doc.ord));
      adapt(c.getSubclusters()).forEach(clone::addSubcluster);
      return clone;
    }).collect(Collectors.toList());
  }

  private List<Cluster<DocumentRef>> runClustering(ClusterServletRequest clusteringRequest,
                                                   ClusteringAlgorithm algorithm,
                                                   LanguageComponents language) {
    IntCursor c = new IntCursor();
    Stream<DocumentRef> stream = clusteringRequest.documents.stream()
        .sequential()
        .map(doc -> new DocumentRef(doc, c.value++));

    return algorithm.cluster(stream, language);
  }

  private ClusterServletRequest parseRequest(HttpServletRequest request) throws IOException, TerminateRequestException {
    try {
      return om.readerFor(ClusterServletRequest.class)
          .readValue(new BufferedInputStream(request.getInputStream()));
    } catch (IOException e) {
      throw new TerminateRequestException(HttpServletResponse.SC_BAD_REQUEST, "Could not parse request body.", e);
    }
  }
}
