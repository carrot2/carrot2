
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

package org.carrot2.core.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Collection;
import java.util.HashMap;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.junit.Assert;

import com.google.common.base.Function;

/**
 * Simple baseline tests that apply to a generic data sources.
 */
public abstract class DocumentSourceTestBase<T extends IDocumentSource> extends
    ProcessingComponentTestBase<T>
{
    /**
     * Runs a query without specifying any additional attributes. The query is run using
     * the {@link SimpleController}.
     * 
     * @return Returns the number of fetched documents. Access
     *         {@link #processingAttributes} map to get hold of the actual documents.
     */
    protected int runQuery()
    {
        return runQuery(getSimpleController(initAttributes));
    }

    /**
     * Runs a query without specifying any additional attributes. The query is run using
     * the {@link CachingController}.
     * 
     * @return Returns the number of fetched documents. Access
     *         {@link #processingAttributes} map to get hold of the actual documents.
     */
    protected int runQueryInCachingController()
    {
        return runQuery(getCachingController(initAttributes));
    }

    /**
     * Runs a query without specifying any additional attributes.
     * 
     * @param controller the {@link IController} to perform the query
     * @return Returns the number of fetched documents. Access
     *         {@link #processingAttributes} map to get hold of the actual documents.
     */
    @SuppressWarnings("unchecked")
    protected int runQuery(IController controller)
    {
        final ProcessingResult result = controller.process(processingAttributes,
            getComponentClass());

        final Collection<Document> documents = (Collection<Document>) processingAttributes
            .get(AttributeNames.DOCUMENTS);
        assertNotNull(result.getDocuments());
        assertSame(result.getDocuments(), documents);
        return documents.size();
    }

    /**
     * Runs a <code>query</code> and asks for <code>results</code> results. The query
     * is run using the {@link SimpleController}.
     * 
     * @return Returns the number of fetched documents. Access
     *         {@link #processingAttributes} map to get hold of the actual documents.
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
     * Transforms {@link Document}s to their ids.
     */
    protected static final Function<Document, Integer> DOCUMENT_TO_ID = new Function<Document, Integer>()
    {
        public Integer apply(Document document)
        {
            return document.getId();
        }
    };

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
