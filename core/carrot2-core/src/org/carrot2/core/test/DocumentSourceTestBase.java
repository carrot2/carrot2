/**
 *
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
 * Simple baseline tests that apply to most (?) data sources.
 */
public abstract class DocumentSourceTestBase<T extends DocumentSource> extends
    ProcessingComponentTestBase<T>
{
    /**
     * Runs a query without specifying any additional attributes.
     * 
     * @return Returns the number of fetched documents. Access {@link #attributes} map to
     *         get hold of the actual documents.
     */
    @SuppressWarnings("unchecked")
    protected int runQuery()
    {
        final ProcessingResult result = controller.process(attributes,
            getComponentClass());

        final Collection<Document> documents = (Collection<Document>) attributes
            .get(AttributeNames.DOCUMENTS);
        assertNotNull(result.getDocuments());
        assertSame(result.getDocuments(), documents);
        return documents.size();
    }

    /**
     * Runs a <code>query</code> and asks for <code>results</code> results.
     * 
     * @return Returns the number of fetched documents. Access {@link #attributes} map to
     *         get hold of the actual documents.
     */
    @SuppressWarnings("unchecked")
    protected final int runQuery(String query, int results)
    {
        attributes.put(AttributeNames.QUERY, query);
        attributes.put(AttributeNames.RESULTS, results);
        return runQuery();
    }

    /**
     * Checks if values of a given field in a collection of {@link Document}s are unique.
     */
    @SuppressWarnings("unchecked")
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
    protected static class DocumentToFieldTransformer implements Function<Document, Object>
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
    protected static Function<Document, Integer> DOCUMENT_TO_ID = new Function<Document, Integer>()
    {
        public Integer apply(Document document)
        {
            return document.getId();
        }
    };

    /**
     * Transforms {@link Document}s to their titles.
     */
    protected static DocumentToFieldTransformer DOCUMENT_TO_TITLE = new DocumentToFieldTransformer(
        Document.TITLE);

    /**
     * Transforms {@link Document}s to their summaries.
     */
    protected static DocumentToFieldTransformer DOCUMENT_TO_SUMMARY = new DocumentToFieldTransformer(
        Document.SUMMARY);

    /**
     * Transforms {@link Document}s to their content URLs.
     */
    protected static DocumentToFieldTransformer DOCUMENT_TO_CONTENT_URL = new DocumentToFieldTransformer(
        Document.CONTENT_URL);
}
