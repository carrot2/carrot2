
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

package org.carrot2.source.lucene;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.RAMDirectory;
import org.carrot2.core.Document;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.test.QueryableDocumentSourceTestBase;
import org.carrot2.util.attribute.AttributeUtils;
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

    @Test
    public void testCustomQuery() throws Exception
    {
        final BooleanQuery query = new BooleanQuery();
        query.add(new TermQuery(new Term("snippet", "data")), Occur.MUST);

        this.processingAttributes.put(AttributeNames.QUERY, query);

        assertThat(runQuery(null, getLargeQuerySize())).as("Number of results")
            .isGreaterThan(10);
    }

    @Test
    public void testAdvancedQueries() throws Exception
    {
        assertThat(runQuery("\"data mining\"", getLargeQuerySize())).as(
            "Number of results").isEqualTo(99);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testMultiEntryField() throws Exception
    {
        runQuery("\"termb\"", getLargeQuerySize());

        final List<Document> list = (List<Document>) super.resultAttributes.get(AttributeNames.DOCUMENTS);
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getSummary()).contains("terma");
        assertThat(list.get(0).getSummary()).contains("termb");
    }
}
