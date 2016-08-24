
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

package org.carrot2.source.lucene;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.RAMDirectory;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.core.test.QueryableDocumentSourceTestBase;
import org.carrot2.util.attribute.AttributeUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests Lucene document source.
 */
public class LuceneDocumentSourceTest extends
    QueryableDocumentSourceTestBase<LuceneDocumentSource>
{
    private static SimpleAnalyzer analyzer;
    private static RAMDirectory directory;

    @BeforeClass
    public static void prepareIndex() throws Exception
    {
        directory = new RAMDirectory();
        analyzer = new SimpleAnalyzer();
        LuceneIndexUtils.createAndPopulateIndex(directory, analyzer);
    }

    @Before
    public void prepareComponent()
    {
        this.initAttributes.put(
            AttributeUtils.getKey(LuceneDocumentSource.class, "directory"), directory);

        this.initAttributes.put(
            AttributeUtils.getKey(SimpleFieldMapper.class, "titleField"), "title");

        this.initAttributes.put(
            AttributeUtils.getKey(SimpleFieldMapper.class, "contentField"), "snippet");

        this.initAttributes.put(
            AttributeUtils.getKey(SimpleFieldMapper.class, "urlField"), "url");

        this.initAttributes.put(
            AttributeUtils.getKey(SimpleFieldMapper.class, "searchFields"),
            Arrays.asList(new String []
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
        return false;
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

    @Test
    public void testCustomFormatter() throws Exception
    {
        this.initAttributes.put(
            AttributeUtils.getKey(SimpleFieldMapper.class, "formatter"),
            SimpleHTMLFormatter.class);

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

    @Test
    public void testCustomQuery() throws Exception
    {
        final BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(new TermQuery(new Term("snippet", "data")), Occur.MUST);

        this.processingAttributes.put(AttributeNames.QUERY, builder.build());

        assertThat(runQuery(null, getLargeQuerySize())).as("Number of results")
            .isGreaterThan(10);
    }

    @Test
    public void testAdvancedQueries() throws Exception
    {
        assertThat(runQuery("\"data mining\"", getLargeQuerySize())).as(
            "Number of results").isEqualTo(99);
    }

    @Test
    public void testMultiEntryField() throws Exception
    {
        runQuery("\"termb\"", getLargeQuerySize());

        final List<Document> list = getDocuments();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getSummary()).contains("terma");
        assertThat(list.get(0).getSummary()).contains("termb");
    }

    /**
     * Test case for CARROT-820.
     */
    @Test
    public void testCatchAllQueryWithHighlighting() throws Exception
    {
        SimpleFieldMapperDescriptor.attributeBuilder(processingAttributes).formatter(
            PlainTextFormatter.class);
        runQuery("*:*", 2);

        final List<Document> list = getDocuments();
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0).getSummary()).isNotEmpty();
        assertThat(list.get(0).getSummary()).isNotEmpty();
    }

    @Test
    public void luceneScorePassing() throws Exception
    {
        final int results = 10;
        assertThat(runQuery("\"data mining\"", results)).as("Number of results")
            .isEqualTo(results);
        for (Document document : getDocuments())
        {
            assertThat(document.getScore()).isNotNull().isGreaterThan(0);
        }
    }

    /**
     * Keeping Lucene documents by default is not a good idea, because it would cause the
     * cache size to grow very quickly.
     */
    @Test
    public void luceneDocumentNotPassedByDefault() throws Exception
    {
        final int results = 10;
        assertThat(runQuery("\"data mining\"", results)).as("Number of results")
            .isEqualTo(results);
        for (Document document : getDocuments())
        {
            for (Object field : document.getFields().values())
            {
                // Lucene Document class is final
                assertThat(field.getClass()).as("Field type").isNotEqualTo(
                    org.apache.lucene.document.Document.class);
            }
        }
    }

    @Test
    public void luceneDocumentPassing() throws Exception
    {
        LuceneDocumentSourceDescriptor.attributeBuilder(processingAttributes)
            .keepLuceneDocuments(true);

        final int results = 10;
        assertThat(runQuery("\"data mining\"", results)).as("Number of results")
            .isEqualTo(results);
        for (Document document : getDocuments())
        {
            assertThat((Object) document.getField(LuceneDocumentSource.LUCENE_DOCUMENT_FIELD))
                .isInstanceOf(org.apache.lucene.document.Document.class);
        }
    }

    @Test
    public void luceneDocumentNotSerialized() throws Exception
    {
        final int results = 2;
        CommonAttributesDescriptor.attributeBuilder(processingAttributes)
            .query("\"data mining\"").results(results);
        LuceneDocumentSourceDescriptor.attributeBuilder(processingAttributes)
            .keepLuceneDocuments(true);
        final ProcessingResult result = getSimpleController(initAttributes).process(
            processingAttributes, LuceneDocumentSource.class);
        assertThat(result.getDocuments().size()).as("Number of results").isEqualTo(
            results);

        final StringWriter json = new StringWriter();
        result.serializeJson(json);
        assertThat(json.toString()).doesNotContain("\"luceneDocument\"");

        final ByteArrayOutputStream xml = new ByteArrayOutputStream();
        result.serialize(xml);
        assertThat(xml.toString("UTF-8")).doesNotContain(
            "org.apache.lucene.document.Document");
    }
}
