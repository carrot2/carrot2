/**
 *
 */
package org.carrot2.core.test;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.junit.Before;

/**
 * A very basic base class for testing {@link ProcessingComponent}s. This class provides
 * an instance of {@link SimpleController}, a map for attributes and a method that
 * initializes them.
 */
public abstract class ProcessingComponentTestBase<T extends ProcessingComponent> extends
    ExternalApiTestBase
{
    /** Simple controller used for tests. */
    protected SimpleController simpleController;

    /** Caching controller used for tests. */
    protected CachingController cachingController;

    /** A map of initialization attributes used for tests. */
    protected Map<String, Object> initAttributes;

    /** A map of processing attributes used for tests. */
    protected Map<String, Object> processingAttributes;

    /**
     * @return Return the class of the component being tested.
     */
    public abstract Class<T> getComponentClass();

    /**
     * Controller and attributes are cleared before every test.
     */
    @Before
    @SuppressWarnings("unchecked")
    public void prepareComponent()
    {
        this.simpleController = new SimpleController();
        this.cachingController = new CachingController();
        this.initAttributes = new HashMap<String, Object>();
        this.processingAttributes = new HashMap<String, Object>();
    }

    /**
     * Returns the documents stored in {@link #processingAttributes}.
     */
    @SuppressWarnings("unchecked")
    protected List<Document> getDocuments()
    {
        return (List<Document>) processingAttributes.get(AttributeNames.DOCUMENTS);
    }
}
