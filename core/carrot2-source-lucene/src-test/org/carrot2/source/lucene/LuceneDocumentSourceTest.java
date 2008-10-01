package org.carrot2.source.lucene;

import static org.carrot2.core.test.SampleDocumentData.DOCUMENTS_DATA_MINING;
import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.RAMDirectory;
import org.carrot2.core.Document;
import org.carrot2.core.test.QueryableDocumentSourceTestBase;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junitext.runners.AnnotationRunner;

/**
 * Tests Lucene document source.
 */
@RunWith(AnnotationRunner.class)
public class LuceneDocumentSourceTest extends
    QueryableDocumentSourceTestBase<LuceneDocumentSource>
{
    private static SimpleAnalyzer analyzer;
    private static RAMDirectory directory;

    @BeforeClass
    public static void prepareIndex() throws Exception
    {
        analyzer = new SimpleAnalyzer();
        directory = new RAMDirectory();

        final IndexWriter w = new IndexWriter(directory, analyzer, true);
        for (Document d : DOCUMENTS_DATA_MINING)
        {
            org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();

            doc.add(new Field("title", (String) d.getField(Document.TITLE), Store.YES,
                Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));

            doc.add(new Field("snippet", (String) d.getField(Document.SUMMARY),
                Store.YES, Field.Index.TOKENIZED, TermVector.WITH_POSITIONS_OFFSETS));

            doc.add(new Field("url", (String) d.getField(Document.CONTENT_URL),
                Store.YES, Field.Index.NO));

            w.addDocument(doc);
        }
        w.close();
    }

    @Override
    public void prepareComponent()
    {
        super.prepareComponent();

        this.initAttributes.put(AttributeUtils.getKey(LuceneDocumentSource.class,
            "directory"), directory);

        this.initAttributes.put(AttributeUtils.getKey(SimpleFieldMapper.class,
            "titleField"), "title");

        this.initAttributes.put(AttributeUtils.getKey(SimpleFieldMapper.class,
            "contentField"), "snippet");

        this.initAttributes.put(AttributeUtils
            .getKey(SimpleFieldMapper.class, "urlField"), "url");

        this.initAttributes.put(AttributeUtils.getKey(SimpleFieldMapper.class,
            "searchFields"), Arrays.asList(new String []
        {
            "title", "snippet"
        }));
    }

    @Override
    public Class<LuceneDocumentSource> getComponentClass()
    {
        return LuceneDocumentSource.class;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return true;
    }

    @Override
    protected String getSmallQueryText()
    {
        return "software";
    }

    @Override
    protected int getSmallQuerySize()
    {
        return 13;
    }

    @Override
    protected String getLargeQueryText()
    {
        return "data mining";
    }

    @Override
    protected int getLargeQuerySize()
    {
        return 100;
    }

    @Ignore
    @Override
    public void testUtfCharacters() throws Exception
    {
        // Ignore this test.
    }

    @Test
    public void testCustomFormatter() throws Exception
    {
        this.initAttributes.put(AttributeUtils.getKey(SimpleFieldMapper.class,
            "formatter"), SimpleHTMLFormatter.class);

        runQuery(getLargeQueryText(), getLargeQuerySize());

        int highlights = 0;
        for (Document d : getDocuments())
        {
            if (((String) d.getField(Document.SUMMARY)).indexOf("") >= 0)
            {
                highlights++;
            }
        }

        assertThat(highlights).as("Number of highlights").isGreaterThan(10);
    }
}
