
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.ambient;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.carrot2.core.Document;
import org.carrot2.core.SimpleController;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.test.DocumentSourceTestBase;
import org.carrot2.source.ambient.AmbientDocumentSource.AmbientTopic;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

/**
 * Test cases for {@link AmbientDocumentSource}.
 */
public class AmbientDocumentSourceTest extends
    DocumentSourceTestBase<AmbientDocumentSource>
{
    @Override
    public Class<AmbientDocumentSource> getComponentClass()
    {
        return AmbientDocumentSource.class;
    }

    @Test
    public void testData()
    {
        assertThat(AmbientDocumentSource.documentsByTopicId).hasSize(
            AmbientDocumentSource.TOPIC_COUNT);
        for (Set<Integer> subtopicIds : AmbientDocumentSource.subtopicIdsByTopicId
            .values())
        {
            assertThat(subtopicIds.size()).isGreaterThan(1);
        }
    }

    @Test
    public void testResultsNumber()
    {
        final int results = 50;
        final SimpleController controller = getSimpleController(initAttributes);
        processingAttributes.put(AttributeUtils.getKey(AmbientDocumentSource.class,
            "topic"), AmbientTopic.AIDA);
        processingAttributes.put(AttributeNames.RESULTS, results);

        runQuery(controller);
        final List<Document> documents = getDocuments();
        assertThat(documents).hasSize(results);
    }

    @Test
    public void testAllTopicsWithoutDocumentsWithoutTopic()
    {
        final int minTopicSize = 1;
        final boolean includeDocumentsWithoutTopic = false;
        checkAllTopics(minTopicSize, includeDocumentsWithoutTopic);
    }

    @Test
    public void testAllTopicsWithDocumentsWithoutTopic()
    {
        final int minTopicSize = 1;
        final boolean includeDocumentsWithoutTopic = true;
        checkAllTopics(minTopicSize, includeDocumentsWithoutTopic);
    }

    @Test
    public void testAllTopicsWithMinTopicSize()
    {
        final int minTopicSize = 3;
        final boolean includeDocumentsWithoutTopic = false;
        checkAllTopics(minTopicSize, includeDocumentsWithoutTopic);
    }

    private void checkAllTopics(final int minTopicSize,
        final boolean includeDocumentsWithoutTopic)
    {
        final SimpleController controller = getSimpleController(initAttributes);
        int topicIndex = 1;
        for (AmbientTopic topic : AmbientDocumentSource.AmbientTopic.values())
        {
            processingAttributes.put(AttributeUtils.getKey(AmbientDocumentSource.class,
                "topic"), topic);
            processingAttributes.put(AttributeUtils.getKey(AmbientDocumentSource.class,
                "includeDocumentsWithoutTopic"), includeDocumentsWithoutTopic);
            processingAttributes.put(AttributeUtils.getKey(AmbientDocumentSource.class,
                "minTopicSize"), minTopicSize);

            runQuery(controller);
            final List<Document> documents = getDocuments();
            checkTopic(documents, topicIndex, minTopicSize, includeDocumentsWithoutTopic);
            topicIndex++;
        }
    }

    private void checkTopic(List<Document> documents, int topicIndex, int minTopicSize,
        boolean includeDocumentsWithoutTopic)
    {
        for (Document document : documents)
        {
            assertThat((String) document.getField(Document.TITLE)).isNotEmpty();
            assertThat((String) document.getField(Document.CONTENT_URL)).isNotEmpty();
            final String topicId = document.getField(Document.TOPIC);
            assertThat(topicId.startsWith(Integer.toString(topicIndex))).isTrue();

            if (!includeDocumentsWithoutTopic)
            {
                assertThat(topicId.endsWith(".0")).isFalse();
            }

            assertThat(AmbientDocumentSource.getTopicLabel(topicId)).isNotEmpty();
        }

        final ListMultimap<String, Document> documentsByTopic = Multimaps.index(
            documents, new Function<Document, String>()
            {
                public String apply(Document document)
                {
                    return document.getField(Document.TOPIC);
                }
            });
        for (String topic : documentsByTopic.keySet())
        {
            assertThat(documentsByTopic.get(topic).size()).isGreaterThanOrEqualTo(
                minTopicSize);
        }
    }
}
