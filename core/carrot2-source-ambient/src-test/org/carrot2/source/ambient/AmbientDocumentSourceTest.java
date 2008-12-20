
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
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
    public void testAllTopics()
    {
        final SimpleController controller = getSimpleController(initAttributes);
        int topicIndex = 1;
        for (AmbientTopic topic : AmbientDocumentSource.AmbientTopic.values())
        {
            processingAttributes.put(AttributeUtils.getKey(AmbientDocumentSource.class,
                "topic"), topic);

            runQuery(controller);
            final List<Document> documents = getDocuments();
            for (Document document : documents)
            {
                assertThat((String) document.getField(Document.TITLE)).isNotEmpty();
                assertThat((String) document.getField(Document.CONTENT_URL)).isNotEmpty();
                final String topicId = document.getField(Document.TOPIC);
                assertThat(topicId.startsWith(Integer.toString(topicIndex))).isTrue();
            }
            topicIndex++;
        }
    }
}
