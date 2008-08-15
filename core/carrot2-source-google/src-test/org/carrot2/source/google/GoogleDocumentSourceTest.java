package org.carrot2.source.google;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.core.test.MultipartDocumentSourceTestBase;
import org.carrot2.source.MultipartSearchEngine.MultipartSearchEngineMetadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junitext.runners.AnnotationRunner;

/**
 * Test cases for {@link GoogleDocumentSource}.
 */
@RunWith(AnnotationRunner.class)
public class GoogleDocumentSourceTest extends
    MultipartDocumentSourceTestBase<GoogleDocumentSource>
{
    @Override
    public Class<GoogleDocumentSource> getComponentClass()
    {
        return GoogleDocumentSource.class;
    }

    @Override
    protected MultipartSearchEngineMetadata getSearchEngineMetadata()
    {
        return GoogleDocumentSource.metadata;
    }

    @Override
    protected int getLargeQuerySize()
    {
        return 32;
    }

    @Override
    protected int getSmallQuerySize()
    {
        return 16;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return true;
    }

    @Test
    public void testHighlightsRemoved()
    {
        runQuery("test", getSmallQuerySize());
        final List<Document> documents = getDocuments();
        for (Document document : documents)
        {
            final String snippet = document.getField(Document.SUMMARY);
            assertThat(snippet).doesNotMatch(".*</?b>.*");
        }
    }
}
