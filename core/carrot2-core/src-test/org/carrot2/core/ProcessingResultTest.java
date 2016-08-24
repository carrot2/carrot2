
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.NullOutputStream;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.CollectionUtils;
import org.carrot2.util.tests.CarrotTestCase;
import org.fest.assertions.Assertions;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Test cases for {@link ProcessingResult}.
 */
public class ProcessingResultTest extends CarrotTestCase
{
    @Test
    public void testSerializationDeserializationAll() throws Exception
    {
        checkSerializationDeserialization(true, true, true);
    }

    @Test
    public void testSerializationDeserializationDocumentsOnly() throws Exception
    {
        checkSerializationDeserialization(true, false, false);
    }

    @Test
    public void testSerializationDeserializationClustersOnly() throws Exception
    {
        checkSerializationDeserialization(false, true, false);
    }
    
    @Test
    public void testSerializationDeserializationAttributesOnly() throws Exception
    {
        checkSerializationDeserialization(false, false, true);
    }

    @Test
    public void testDocumentDeserializationFromLegacyXml() throws Exception
    {
        final String query = "apple computer";
        final String title = "Apple Computer, Inc.";
        final String snippet = "Macintosh hardware, software, and Internet tools.";
        final String url = "http:// www.apple.com/";

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<searchresult>\n");
        xml.append("<query>" + query + "</query>\n");
        xml.append("<document id=\"0\">");
        xml.append("<title>" + title + "</title>\n");
        xml.append("<snippet>" + snippet + "</snippet>\n");
        xml.append("<url>" + url + "</url>\n");
        xml.append("</document>\n");
        xml.append("</searchresult>\n");

        final ProcessingResult deserialized = ProcessingResult
            .deserialize(new ByteArrayInputStream(xml.toString().getBytes("UTF-8")));

        assertNotNull(deserialized);
        assertNotNull(deserialized.getAttributes());

        Document deserializedDocument = CollectionUtils.getFirst(deserialized
            .getDocuments());
        assertEquals(title, deserializedDocument.getField(Document.TITLE));
        assertEquals(snippet, deserializedDocument.getField(Document.SUMMARY));
        assertEquals(url, deserializedDocument.getField(Document.CONTENT_URL));
        assertNull(deserializedDocument.getField(Document.LANGUAGE));
        Assertions.assertThat(deserialized.getAttributes().get(AttributeNames.QUERY))
            .isEqualTo(query);
    }
    
    @Test
    public void deserializeStringDocumentIds() throws Exception
    {
        final String title = "Apple Computer, Inc.";
        final String id = "cafe00f0";
        
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<searchresult>\n");
        xml.append("<document id=\"" + id + "\">");
        xml.append("<title>" + title + "</title>\n");
        xml.append("</document>\n");
        xml.append("</searchresult>\n");
        
        final ProcessingResult deserialized = ProcessingResult
            .deserialize(new ByteArrayInputStream(xml.toString().getBytes("UTF-8")));
        
        assertNotNull(deserialized);
        assertNotNull(deserialized.getAttributes());
        
        Document deserializedDocument = CollectionUtils.getFirst(deserialized
            .getDocuments());
        assertEquals(title, deserializedDocument.getField(Document.TITLE));
        assertEquals(id, deserializedDocument.getStringId());
    }

    @Test
    public void testDocumentDeserializationLanguageByIsoCode() throws Exception
    {
        final LanguageCode language = LanguageCode.POLISH;
        assertThat(
            ProcessingResult.deserialize(documentXml(language.getIsoCode()))
                .getDocuments().get(0).getLanguage()).isEqualTo(language);

    }

    @Test
    public void testDocumentDeserializationLanguageByEnumCode() throws Exception
    {
        final LanguageCode language = LanguageCode.POLISH;
        assertThat(
            ProcessingResult.deserialize(documentXml(language.name())).getDocuments()
                .get(0).getLanguage()).isEqualTo(language);

    }

    private InputStream documentXml(String language) throws Exception
    {
        final StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<searchresult>\n");
        xml.append("<document id=\"0\" language=\"" + language + "\">");
        xml.append("</document>\n");
        xml.append("</searchresult>\n");
        return new ByteArrayInputStream(xml.toString().getBytes("UTF8"));
    }

    @Test
    public void testClusterDeserializationFromLegacyXml() throws Exception
    {
        final String query = "apple computer";

        final String title = "Apple Computer, Inc.";
        final String snippet = "Macintosh hardware, software, and Internet tools.";
        final String url = "http:// www.apple.com/";

        final int documentCount = 3;

        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<searchresult>");
        xml.append("<query>" + query + "</query>\n");
        for (int i = 0; i < documentCount; i++)
        {
            xml.append("<document id=\"" + i + "\">");
            xml.append("<title>" + title + i + "</title>\n");
            xml.append("<snippet>" + snippet + i + "</snippet>\n");
            xml.append("<url>" + url + i + "</url>\n");
            xml.append("</document>\n");
        }
        xml.append("<group score=\"1.0\">");
        xml.append("<title>");
        xml.append("<phrase>Data Mining Techniques</phrase>");
        xml.append("<phrase>Lectures</phrase>");
        xml.append("</title>");
        xml.append("<group>");
        xml.append("<title>");
        xml.append("<phrase>Research</phrase>");
        xml.append("</title>");
        xml.append("<document refid=\"0\"/>");
        xml.append("<document refid=\"1\"/>");
        xml.append("</group>");
        xml.append("</group>");
        xml.append("<group score=\"0.55\">");
        xml.append("<title>");
        xml.append("<phrase>Software</phrase>");
        xml.append("</title>");
        xml.append("<document refid=\"1\"/>");
        xml.append("<document refid=\"2\"/>");
        xml.append("</group>");
        xml.append("</searchresult>\n");

        final ProcessingResult deserialized = ProcessingResult
            .deserialize(new ByteArrayInputStream(xml.toString().getBytes("UTF-8")));

        assertNotNull(deserialized);
        assertNotNull(deserialized.getAttributes());

        // Check documents
        assertThatDocuments(deserialized.getDocuments()).hasSize(documentCount);
        int index = 0;
        final List<Document> documents = deserialized.getDocuments();
        for (Document document : documents)
        {
            assertEquals(title + index, document.getField(Document.TITLE));
            assertEquals(snippet + index, document.getField(Document.SUMMARY));
            assertEquals(url + index, document.getField(Document.CONTENT_URL));
            index++;
        }

        // Check clusters
        final List<Cluster> clusters = deserialized.getClusters();

        final Cluster clusterA = new Cluster();
        clusterA.addPhrases("Data Mining Techniques", "Lectures");
        clusterA.setAttribute(Cluster.SCORE, 1.0);

        final Cluster clusterAA = new Cluster();
        clusterAA.addPhrases("Research");
        clusterAA.addDocuments(documents.get(0), documents.get(1));
        clusterA.addSubclusters(clusterAA);

        final Cluster clusterB = new Cluster();
        clusterB.addPhrases("Software");
        clusterB.setAttribute(Cluster.SCORE, 0.55);
        clusterB.addDocuments(documents.get(1), documents.get(2));

        assertThatClusters(clusters).isEquivalentTo(
            Lists.newArrayList(clusterA, clusterB));
        Assertions.assertThat(deserialized.getAttributes().get(AttributeNames.QUERY))
            .isEqualTo(query);
    }

    @Test
    public void testJsonSerializationAll() throws IOException
    {
        final ProcessingResult result = prepareProcessingResult();
        final JsonNode root = getJsonRootNode(result, null, true, true, true);

        checkJsonQuery(root);
        checkJsonClusters(result, root);
        checkJsonDocuments(result, root);
        Assertions.assertThat(root.get("results")).isNotNull();
    }

    @Test
    public void testJsonSerializationWithCallback() throws IOException
    {
        final String callback = "callback";
        final ProcessingResult result = prepareProcessingResult();

        final StringWriter json = new StringWriter();
        result.serializeJson(json, callback, true, true);
        final String jsonString = json.toString();

        Assertions.assertThat(jsonString).startsWith(callback + "(").endsWith(");");

        final String data = jsonString.substring(callback.length() + 1,
            jsonString.length() - 2);
        final JsonNode root = getJsonRootNode(data);

        checkJsonQuery(root);
        checkJsonClusters(result, root);
        checkJsonDocuments(result, root);
        Assertions.assertThat(root.get("results")).isNotNull();
    }

    @Test
    public void testJsonSerializationDocumentsOnly() throws IOException
    {
        final ProcessingResult result = prepareProcessingResult();
        final JsonNode root = getJsonRootNode(result, null, true, false, false);

        checkJsonQuery(root);
        checkJsonDocuments(result, root);
        Assertions.assertThat(root.get("clusters")).isNull();
        Assertions.assertThat(root.get("results")).isNull();
    }

    @Test
    public void testJsonSerializationClustersOnly() throws IOException
    {
        final ProcessingResult result = prepareProcessingResult();
        final JsonNode root = getJsonRootNode(result, null, false, true, false);

        checkJsonQuery(root);
        checkJsonClusters(result, root);
        Assertions.assertThat(root.get("documents")).isNull();
        Assertions.assertThat(root.get("results")).isNull();
    }

    
    @Test
    public void testJsonSerializationAttributesOnly() throws IOException
    {
        final ProcessingResult result = prepareProcessingResult();
        final JsonNode root = getJsonRootNode(result, null, false, false, true);
        
        checkJsonQuery(root);
        Assertions.assertThat(root.get("documents")).isNull();
        Assertions.assertThat(root.get("clusters")).isNull();
        Assertions.assertThat(root.get("results")).isNotNull();
    }
    
    @Test
    public void testNoFalseJunkGroupAttribute() throws Exception
    {
        Cluster a, b, c;
        final HashMap<String, Object> attrs = Maps.newHashMap();
        attrs.put(AttributeNames.CLUSTERS, Arrays.asList(
            a = new Cluster("a"),
            b = new Cluster("b"),
            c = new Cluster("c")));

        b.setOtherTopics(false);
        c.setOtherTopics(true);

        ProcessingResult pr = new ProcessingResult(attrs);
        pr = ProcessingResult.deserialize(pr.serialize());
        
        assertEquals("a", (a = pr.getClusters().get(0)).getLabel());
        assertEquals("b", (b = pr.getClusters().get(1)).getLabel());
        assertEquals("c", (c = pr.getClusters().get(2)).getLabel());
        assertThat((Object) a.getAttribute(Cluster.OTHER_TOPICS)).isNull();
        assertThat((Object) b.getAttribute(Cluster.OTHER_TOPICS)).isNull();
        assertThat((Object) c.getAttribute(Cluster.OTHER_TOPICS)).isEqualTo(Boolean.TRUE);
    }

    private void checkJsonQuery(final JsonNode root)
    {
        Assertions.assertThat(root.get("query").textValue()).isEqualTo("query");
    }

    private void checkJsonClusters(final ProcessingResult result, final JsonNode root)
    {
        final JsonNode clusters = root.get("clusters");
        Assertions.assertThat(clusters).isNotNull();
        final ArrayList<JsonNode> clusterNodes = Lists.newArrayList(clusters.elements());
        Assertions.assertThat(clusterNodes).hasSize(result.getClusters().size());
    }

    private void checkJsonDocuments(final ProcessingResult result, final JsonNode root)
    {
        final JsonNode documents = root.get("documents");
        Assertions.assertThat(documents).isNotNull();
        final ArrayList<JsonNode> documentNodes = Lists.newArrayList(documents.elements());
        Assertions.assertThat(documentNodes).hasSize(result.getDocuments().size());
    }

    private JsonNode getJsonRootNode(final ProcessingResult result, String callback,
        boolean saveDocuments, boolean saveClusters, boolean saveAttributes) throws IOException,
        JsonParseException
    {
        return getJsonRootNode(getJsonString(result, callback, saveDocuments,
            saveClusters, saveAttributes));
    }

    private String getJsonString(final ProcessingResult result, String callback,
        boolean saveDocuments, boolean saveClusters, boolean saveAttributes) throws IOException
    {
        final StringWriter json = new StringWriter() {
          @Override
          public void close() throws IOException {
            throw new IOException("Should not be calling close.");
          }
        };
        result.serializeJson(json, callback, false, saveDocuments, saveClusters, saveAttributes);
        return json.toString();
    }

    private JsonNode getJsonRootNode(final String jsonString) throws IOException,
        JsonParseException
    {
        final JsonParser jsonParser = new JsonFactory().createParser(new StringReader(jsonString));
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode root = mapper.readTree(jsonParser);
        return root;
    }

    private void checkSerializationDeserialization(boolean documentsDeserialized,
        boolean clustersDeserialized, boolean attributesDeserialized) throws Exception
    {
        final ProcessingResult sourceProcessingResult = prepareProcessingResult();

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sourceProcessingResult.serialize(new NullOutputStream());
        sourceProcessingResult.serialize(outputStream, documentsDeserialized,
            clustersDeserialized, attributesDeserialized);
        CloseableUtils.close(outputStream);

        final ProcessingResult deserialized = ProcessingResult
            .deserialize(new ByteArrayInputStream(outputStream.toByteArray()));

        assertNotNull(deserialized);
        assertNotNull(deserialized.getAttributes());

        if (documentsDeserialized)
        {
            assertThatDocuments(deserialized.getDocuments()).isEquivalentTo(
                sourceProcessingResult.getDocuments());
        }
        else
        {
            Assertions.assertThat(deserialized.getDocuments()).isNull();
        }

        if (clustersDeserialized)
        {
            assertThatClusters(deserialized.getClusters()).isEquivalentTo(
                sourceProcessingResult.getClusters(), documentsDeserialized);
        }
        else
        {
            Assertions.assertThat(deserialized.getClusters()).isNull();
        }

        if (attributesDeserialized)
        {
            Assertions.assertThat((Object) deserialized.getAttribute(AttributeNames.RESULTS))
                .isEqualTo(sourceProcessingResult.getAttribute(AttributeNames.RESULTS));
        }
        else
        {
            Assertions.assertThat((Object) deserialized.getAttribute(AttributeNames.RESULTS))
                .isNull();
        }

        Assertions.assertThat(deserialized.getAttributes().get(AttributeNames.QUERY))
            .isEqualTo(sourceProcessingResult.getAttributes().get(AttributeNames.QUERY));
    }

    private ProcessingResult prepareProcessingResult()
    {
        final List<Document> documents = Lists.newArrayList(new Document("Test title 1",
            "Test snippet 1", "http://test1.com"), new Document("Test title 2",
            "Test snippet 2", "http://test2.com/test"), new Document("Test title 3",
            "Test snippet 3. Some more words and <b>html</b>", "http://test2.com"),
            new Document("Other", "Other", "Other"));
        final Map<String, Object> attributes = Maps.newHashMap();
        attributes.put(AttributeNames.DOCUMENTS, documents);

        final Document document = documents.get(0);
        document.setSources(Lists.newArrayList("s1", "s2"));
        document.setField("testString", "test");
        document.setField("testInteger", 10);
        document.setField("testDouble", 10.3);
        document.setField("testBoolean", true);
        document.setLanguage(LanguageCode.POLISH);
        document.setSources(Lists.newArrayList("s1", "s2"));
        Document.assignDocumentIds(documents);

        final Cluster clusterA = new Cluster();
        clusterA.addPhrases("Label 1", "Label 2");
        clusterA.setAttribute(Cluster.SCORE, 1.0);
        clusterA.setAttribute("testString", "test");
        clusterA.setAttribute("testInteger", 10);
        clusterA.setAttribute("testDouble", 10.3);
        clusterA.setAttribute("testBoolean", true);

        final Cluster clusterAA = new Cluster();
        clusterAA.addPhrases("Label 3 zażółć gęślą jaźń");
        clusterAA.addDocuments(documents.get(0), documents.get(1));
        clusterA.addSubclusters(clusterAA);

        final Cluster clusterB = new Cluster();
        clusterB.addPhrases("Label 4");
        clusterB.setAttribute(Cluster.SCORE, 0.55);
        clusterB.addDocuments(documents.get(1), documents.get(2));

        final Cluster clusterO = new Cluster();
        clusterO.setOtherTopics(true);
        clusterO.addPhrases(Cluster.OTHER_TOPICS_LABEL);
        clusterO.addDocuments(documents.get(3));

        final List<Cluster> clusters = Lists.newArrayList(clusterA, clusterB, clusterO);
        attributes.put(AttributeNames.CLUSTERS, clusters);

        attributes.put(AttributeNames.QUERY, "query");

        attributes.put(AttributeNames.RESULTS, 120);

        return new ProcessingResult(attributes);
    }
}
