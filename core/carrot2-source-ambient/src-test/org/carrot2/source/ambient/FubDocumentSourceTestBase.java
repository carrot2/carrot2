
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

package org.carrot2.source.ambient;

import java.util.List;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.test.DocumentSourceTestBase;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.Test;

import org.carrot2.shaded.guava.common.base.Function;
import org.carrot2.shaded.guava.common.collect.ListMultimap;
import org.carrot2.shaded.guava.common.collect.Multimaps;

/**
 * Test cases for {@link FubDocumentSource}s.
 */
public abstract class FubDocumentSourceTestBase<T extends IDocumentSource> extends
    DocumentSourceTestBase<T>
{
    protected abstract FubTestCollection getData();

    protected abstract int getTopicCount();

    protected abstract Object [] getAllTopics();

    @Test
    public void testData()
    {
        assertThat(getData().topicCount).isEqualTo(getTopicCount());

        assertThat(getData().documentsByTopicId).hasSize(getTopicCount());
        for (List<Document> documents : getData().documentsByTopicId.values())
        {
            assertThat(documents.size()).isGreaterThan(1);
        }

        for (Integer size : getData().subtopicSizes.values())
        {
            assertThat(size).isGreaterThan(0);
        }

        for (String label : getData().subtopicLabels.values())
        {
            assertThat(label).isNotEmpty();
        }
    }

    @Test
    public void testResultsNumber()
    {
        final int results = 50;
        final Controller controller = getSimpleController(initAttributes);
        processingAttributes.put(AttributeUtils.getKey(getComponentClass(), "topic"),
            getAllTopics()[0]);
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
        final Controller controller = getSimpleController(initAttributes);
        int topicIndex = 1;
        for (Object topic : getAllTopics())
        {
            processingAttributes.put(AttributeUtils.getKey(getComponentClass(), "topic"),
                topic);
            processingAttributes.put(AttributeUtils.getKey(FubDocumentSource.class,
                "includeDocumentsWithoutTopic"), includeDocumentsWithoutTopic);
            processingAttributes.put(AttributeUtils.getKey(FubDocumentSource.class,
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
            final String topicId = FubTestCollection.getTopic(document);
            assertThat(topicId.startsWith(Integer.toString(topicIndex))).isTrue();

            if (!includeDocumentsWithoutTopic)
            {
                assertThat(topicId.endsWith(".0")).isFalse();
                assertThat(getData().getTopicLabel(topicId)).isNotEmpty();
            }
        }

        final ListMultimap<String, Document> documentsByTopic = Multimaps.index(
            documents, new Function<Document, String>()
            {
                public String apply(Document document)
                {
                    return FubTestCollection.getTopic(document);
                }
            });
        for (String topic : documentsByTopic.keySet())
        {
            assertThat(documentsByTopic.get(topic).size()).as("Topic " + topic + " size")
                .isGreaterThanOrEqualTo(minTopicSize);
        }
    }
}
