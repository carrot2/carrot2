
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

package org.carrot2.core.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.carrot2.core.Controller;
import org.carrot2.core.Document;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.BindableMetadata;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import org.carrot2.shaded.guava.common.base.Function;

/**
 * Simple baseline tests that apply to a generic data sources.
 */
public abstract class DocumentSourceTestBase<T extends IDocumentSource> extends
    ProcessingComponentTestBase<T>
{
    /**
     * Result of the last call to any of the {@link #runQuery()} methods.
     */
    protected ProcessingResult result;

    /**
     * Processing components should be bindable, so their metadata should 
     * always be available.
     */
    @Test
    public void testMetadataAvailable()
    {
        Class<? extends IDocumentSource> c = getComponentClass();
        Assume.assumeTrue(c.getAnnotation(Bindable.class) != null);

        BindableMetadata metadata = BindableMetadata.forClassWithParents(c);
        assertNotNull(metadata);
        assertNotNull(metadata.getAttributeMetadata());
    }

    /**
     * Runs a query without specifying any additional attributes. The query is run using
     * a simple {@link Controller}.
     * 
     * @return Returns the number of fetched documents. Access
     *         {@link #resultAttributes} map to get hold of the actual documents.
     */
    protected int runQuery()
    {
        return runQuery(getSimpleController(initAttributes));
    }

    /**
     * Runs a query without specifying any additional attributes. The query is run using
     * a {@link Controller} with caching.
     * 
     * @return Returns the number of fetched documents. Access
     *         {@link #resultAttributes} map to get hold of the actual documents.
     */
    @SuppressWarnings("unchecked")
    protected int runQueryInCachingController()
    {
        return runQuery(getCachingController(initAttributes));
    }

    /**
     * Runs a query without specifying any additional attributes.
     * 
     * @param controller the {@link Controller} to perform the query
     * @return Returns the number of fetched documents. Access
     *         {@link #resultAttributes} map to get hold of the actual documents.
     */
    @SuppressWarnings("unchecked")
    protected int runQuery(Controller controller)
    {
        result = controller.process(processingAttributes, getComponentClass());
        resultAttributes = result.getAttributes();

        final List<Document> documents = (List<Document>) resultAttributes.get(AttributeNames.DOCUMENTS);
        assertNotNull(result.getDocuments());
        assertThat(result.getDocuments()).isEqualTo(documents);
        return documents.size();
    }

    /**
     * Runs a <code>query</code> and asks for <code>results</code> results. The query is
     * run using a simple {@link Controller}.
     * 
     * @return Returns the number of fetched documents. Access
     *         {@link #resultAttributes} map to get hold of the actual documents.
     */
    protected final int runQuery(String query, int results)
    {
        if (query != null)
        {
            processingAttributes.put(AttributeNames.QUERY, query);
        }
        processingAttributes.put(AttributeNames.RESULTS, results);
        return runQuery();
    }

    /**
     * Checks if values of a given field in a collection of {@link Document}s are unique.
     */
    protected final void assertFieldUnique(Collection<Document> result, String fieldName)
    {
        final HashMap<Object, Integer> values = new HashMap<Object, Integer>();

        final StringBuilder builder = new StringBuilder();
        int i = 0;
        for (final Document d : result)
        {
            final Object key = d.getField(fieldName);
            if (values.containsKey(key))
            {
                builder.append("Key: " + key + ", in docs: " + values.get(key) + ", " + i
                    + "\n");
            }
            else
            {
                values.put(key, i);
            }

            i++;
        }

        if (builder.length() > 0)
        {
            Assert.fail("Field values are not unique, offending fields: \n"
                + builder.toString());
        }
    }

    /**
     * Transforms {@link Document}s to their individual fields.
     */
    protected static class DocumentToFieldTransformer implements
        Function<Document, Object>
    {
        /** Field name */
        private final String fieldName;

        /**
         * Builds a transformer with the provided field name.
         */
        public DocumentToFieldTransformer(String fieldName)
        {
            this.fieldName = fieldName;
        }

        public Object apply(Document document)
        {
            return document.getField(fieldName);
        }
    }

    /**
     * Transforms {@link Document}s to their titles.
     */
    protected static final DocumentToFieldTransformer DOCUMENT_TO_TITLE = new DocumentToFieldTransformer(
        Document.TITLE);

    /**
     * Transforms {@link Document}s to their summaries.
     */
    protected static final DocumentToFieldTransformer DOCUMENT_TO_SUMMARY = new DocumentToFieldTransformer(
        Document.SUMMARY);

    /**
     * Transforms {@link Document}s to their content URLs.
     */
    protected static final DocumentToFieldTransformer DOCUMENT_TO_CONTENT_URL = new DocumentToFieldTransformer(
        Document.CONTENT_URL);
}
