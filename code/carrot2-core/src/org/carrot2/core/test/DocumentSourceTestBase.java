/**
 *
 */
package org.carrot2.core.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Collection;
import java.util.HashMap;

import org.carrot2.core.*;
import org.junit.Assert;


/**
 * Simple baseline tests that apply to most (?) data sources.
 */
public abstract class DocumentSourceTestBase<T extends DocumentSource> extends
    ProcessingComponentTestBase<T>
{
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

        final ProcessingResult result = controller.process(attributes,
            getComponentClass());

        final Collection<Document> documents = (Collection<Document>) attributes
            .get(AttributeNames.DOCUMENTS);
        assertNotNull(result.getDocuments());
        assertSame(result.getDocuments(), documents);

        return documents.size();
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
}
