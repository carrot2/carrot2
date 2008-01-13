/**
 * 
 */
package org.carrot2.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.carrot2.core.controller.SimpleController;
import org.junit.Assert;
import org.junit.Before;


/**
 * Simple baseline tests that apply to most (?) data sources.
 */
public abstract class DocumentSourceTest<T extends DocumentSource>
{
    /** Controller used for tests. */
    protected SimpleController controller;
    
    /** A map of parameters used for tests. */
    protected Map<String, Object> parameters;
    
    /** A map of attributes used for tests. */
    protected Map<String, Object> attributes;

    /**
     * @return Return the class of the component being tested.
     */
    public abstract Class<? extends DocumentSource> getComponentClass();

    /**
     * Controller, attributes and parameters are cleared before every test.
     */
    @Before
    public void prepareComponent()
    {
        this.controller = new SimpleController();
        this.parameters = new HashMap<String, Object>();
        this.attributes = new HashMap<String, Object>();
    }
    
    /**
     * Runs a <code>query</code> and asks for <code>results</code> results.
     * 
     * @return Returns the number of fetched documents. Access {@link #attributes} map
     * to get hold of the actual documents.
     */
    @SuppressWarnings("unchecked")
    protected final int runQuery(String query, int results)
    {
        parameters.put("query", query);
        parameters.put("results", results);

        final ProcessingResult result = controller.process(parameters, attributes, getComponentClass());

        final Collection<Document> documents = (Collection<Document>) attributes.get("documents");
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
        final HashSet values = new HashSet();
        for (Document d : result)
        {
            final Object value = d.getField(fieldName); 
            if (values.contains(value)) {
                Assert.fail("Field values are not unique, offending field value: " 
                    + d.getField(fieldName));
            }
            values.add(value);
        }
    }
}
