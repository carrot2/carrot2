/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThatClusters;
import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThatDocuments;
import static org.fest.assertions.Assertions.assertThat;

import java.io.*;
import java.util.*;

import org.apache.commons.io.output.NullOutputStream;
import org.carrot2.core.ProcessingResult.Rss;
import org.carrot2.core.ProcessingResult.Rss.RssItem;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.CollectionUtils;
import org.codehaus.jackson.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.fest.assertions.Assertions;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Test cases for {@link ProcessingResult}.
 */
public class ProcessingResultTest
{
    @Test
    public void testSerializationDeserializationAll() throws Exception
    {
        checkSerializationDeserialization(true, true);
    }

    @Test
    public void testSerializationDeserializationDocumentsOnly() throws Exception
    {
        checkSerializationDeserialization(true, false);
    }

    @Test
    public void testSerializationDeserializationClustersOnly() throws Exception
    {
        checkSerializationDeserialization(false, true);
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
        Assertions.assertThat(deserialized.getAttributes().get(AttributeNames.QUERY))
            .isEqualTo(query);
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
        final JsonNode root = getJsonRootNode(result, null, true, true);

        checkJsonQuery(root);
        checkJsonClusters(result, root);
        checkJsonDocuments(result, root);
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

        final String data = jsonString.substring(callback.length() + 1, jsonString
            .length() - 2);
        final JsonNode root = getJsonRootNode(data);

        checkJsonQuery(root);
        checkJsonClusters(result, root);
        checkJsonDocuments(result, root);
    }

    @Test
    public void testJsonSerializationDocumentsOnly() throws IOException
    {
        final ProcessingResult result = prepareProcessingResult();
        final JsonNode root = getJsonRootNode(result, null, true, false);

        checkJsonQuery(root);
        checkJsonDocuments(result, root);
        Assertions.assertThat(root.get("clusters")).isNull();
    }

    @Test
    public void testJsonSerializationClustersOnly() throws IOException
    {
        final ProcessingResult result = prepareProcessingResult();
        final JsonNode root = getJsonRootNode(result, null, false, true);

        checkJsonQuery(root);
        checkJsonClusters(result, root);
        Assertions.assertThat(root.get("documents")).isNull();
    }

    private void checkJsonQuery(final JsonNode root)
    {
        Assertions.assertThat(root.get("query").getTextValue()).isEqualTo("query");
    }

    private void checkJsonClusters(final ProcessingResult result, final JsonNode root)
    {
        final JsonNode clusters = root.get("clusters");
        Assertions.assertThat(clusters).isNotNull();
        final ArrayList<JsonNode> clusterNodes = Lists.newArrayList(clusters
            .getElements());
        Assertions.assertThat(clusterNodes).hasSize(result.getClusters().size());
    }

    private void checkJsonDocuments(final ProcessingResult result, final JsonNode root)
    {
        final JsonNode documents = root.get("documents");
        Assertions.assertThat(documents).isNotNull();
        final ArrayList<JsonNode> documentNodes = Lists.newArrayList(documents
            .getElements());
        Assertions.assertThat(documentNodes).hasSize(result.getDocuments().size());
    }

    private JsonNode getJsonRootNode(final ProcessingResult result, String callback,
        boolean saveDocuments, boolean saveClusters) throws IOException,
        JsonParseException
    {
        return getJsonRootNode(getJsonString(result, callback, saveDocuments,
            saveClusters));
    }

    private String getJsonString(final ProcessingResult result, String callback,
        boolean saveDocuments, boolean saveClusters) throws IOException
    {
        final StringWriter json = new StringWriter();
        result.serializeJson(json, callback, false, saveDocuments, saveClusters);
        return json.toString();
    }

    private JsonNode getJsonRootNode(final String jsonString) throws IOException,
        JsonParseException
    {
        final JsonParser jsonParser = new JsonFactory()
            .createJsonParser(new StringReader(jsonString));
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode root = mapper.readTree(jsonParser);
        return root;
    }

    private void checkSerializationDeserialization(boolean documentsDeserialized,
        boolean clustersDeserialized) throws Exception
    {
        final ProcessingResult sourceProcessingResult = prepareProcessingResult();

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sourceProcessingResult.serialize(new NullOutputStream());
        sourceProcessingResult.serialize(outputStream, documentsDeserialized,
            clustersDeserialized);
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

        Assertions.assertThat(deserialized.getAttributes().get(AttributeNames.QUERY))
            .isEqualTo(sourceProcessingResult.getAttributes().get(AttributeNames.QUERY));
    }

    @Test
    public void testProcessingResultToRss() throws Exception
    {
        final ProcessingResult result = prepareProcessingResult();

        final Cluster clusterA = result.getClusters().get(0);
        final Cluster clusterAA = result.getClusters().get(0).getSubclusters().get(0);
        final Cluster clusterB = result.getClusters().get(1);

        final Rss rss = Rss.from(result, true);

        assertThat(rss).isNotNull();
        assertThat(rss.channels).isNotEmpty();

        final RssItem channel = rss.channels.get(0);
        final String query = (String) result.getAttribute(AttributeNames.QUERY);
        assertThat(channel.title).isEqualTo(query);
        assertThat(channel.description).contains(query);
        assertThat(channel.categories).isEmpty();

        final RssItem item1 = channel.items.get(0);
        final RssItem item2 = channel.items.get(1);
        final RssItem item3 = channel.items.get(2);
        final RssItem item4 = channel.items.get(3);

        checkDocumentAndItem(item1, result.getDocuments().get(0));
        checkDocumentAndItem(item2, result.getDocuments().get(1));
        checkDocumentAndItem(item3, result.getDocuments().get(2));
        checkDocumentAndItem(item4, result.getDocuments().get(3));

        assertThat(item1.categories).contains(clusterA.getLabel(), clusterAA.getLabel());
        assertThat(item1.categories).contains(
            result.getDocuments().get(0).getSources().toArray());
        assertThat(item2.categories).containsExactly(clusterA.getLabel(),
            clusterAA.getLabel(), clusterB.getLabel());
        assertThat(item3.categories).containsExactly(clusterB.getLabel());
        assertThat(item4.categories).isEmpty();
    }

    private void checkDocumentAndItem(RssItem item, Document document)
    {
        assertThat(item.title).isEqualTo(document.getTitle());
        assertThat(item.description).isEqualTo(document.getSummary());
        assertThat(item.link).isEqualTo(document.getContentUrl());
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
        document.id = 3; // assign an id so that the max id is larger than the list size
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

        return new ProcessingResult(attributes);
    }
}
