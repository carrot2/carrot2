
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
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
import java.util.*;

import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.CollectionUtils;
import org.carrot2.util.tests.CarrotTestCase;
import org.fest.assertions.Assertions;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test cases for {@link ProcessingResult}.
 */
public class ProcessingResultTest extends CarrotTestCase
{
    @Test
    public void testNoFalseJunkGroupAttribute() throws Exception
    {
        Cluster a, b, c;
        final HashMap<String, Object> attrs = new HashMap<>();
        attrs.put(AttributeNames.CLUSTERS, Arrays.asList(
            a = new Cluster("a"),
            b = new Cluster("b"),
            c = new Cluster("c")));

        b.setOtherTopics(false);
        c.setOtherTopics(true);

        ProcessingResult pr = new ProcessingResult(attrs);
        
        assertEquals("a", (a = pr.getClusters().get(0)).getLabel());
        assertEquals("b", (b = pr.getClusters().get(1)).getLabel());
        assertEquals("c", (c = pr.getClusters().get(2)).getLabel());
        assertThat((Object) a.getAttribute(Cluster.OTHER_TOPICS)).isNull();
        assertThat((Object) b.getAttribute(Cluster.OTHER_TOPICS)).isNull();
        assertThat((Object) c.getAttribute(Cluster.OTHER_TOPICS)).isEqualTo(Boolean.TRUE);
    }

    private ProcessingResult prepareProcessingResult()
    {
        final List<Document> documents = Arrays.asList(
            new Document("Test title 1", "Test snippet 1", "http://test1.com"),
            new Document("Test title 2", "Test snippet 2", "http://test2.com/test"),
            new Document("Test title 3", "Test snippet 3. Some more words and <b>html</b>", "http://test2.com"),
            new Document("Other", "Other", "Other"));
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(AttributeNames.DOCUMENTS, documents);

        final Document document = documents.get(0);
        document.setSources(new ArrayList<>(Arrays.asList("s1", "s2")));
        document.setField("testString", "test");
        document.setField("testInteger", 10);
        document.setField("testDouble", 10.3);
        document.setField("testBoolean", true);
        document.setLanguage(LanguageCode.POLISH);
        document.setSources(new ArrayList<>(Arrays.asList("s1", "s2")));
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

        final List<Cluster> clusters = Arrays.asList(clusterA, clusterB, clusterO);
        attributes.put(AttributeNames.CLUSTERS, clusters);

        attributes.put(AttributeNames.QUERY, "query");

        attributes.put(AttributeNames.RESULTS, 120);

        return new ProcessingResult(attributes);
    }
}
