/**
 *
 */
package org.carrot2.core.test;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.junit.After;
import org.junit.Before;

/**
 * A very basic base class for testing {@link ProcessingComponent}s. This class provides
 * an instance of a {@link Controller}, a map for attributes and a method that
 * initializes them.
 */
public abstract class ProcessingComponentTestBase<T extends ProcessingComponent> extends
    ExternalApiTestBase
{
    /** Simple controller used for tests. */
    private SimpleController simpleController;

    /** Caching controller used for tests. */
    private CachingController cachingController;

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
        this.initAttributes = new HashMap<String, Object>();
        this.processingAttributes = new HashMap<String, Object>();
    }
    
    /**
     * Cleanup.
     */
    @After
    @SuppressWarnings("unchecked")
    public void cleanup()
    {
        if (simpleController != null)
        {
            this.simpleController.dispose();
        }
        
        if (cachingController != null)
        {
            this.cachingController.dispose();
        }
    }

    /**
     * Return an instance of a {@link SimpleController}, initializing it on the way.
     */
    protected final SimpleController getSimpleController(Map<String,Object> initAttributes)
    {
        if (this.simpleController != null)
        {
            throw new RuntimeException("One simple controller per test case, please.");
        }
        
        simpleController = new SimpleController();
        simpleController.init(initAttributes);
        
        return simpleController;
    }

    /**
     * Return an instance of a {@link CachedController}, initializing it on the way.
     */
    @SuppressWarnings("unchecked")
    protected final CachingController getCachingController(Map<String,Object> initAttributes)
    {
        if (this.cachingController != null)
        {
            throw new RuntimeException("One simple controller per test case, please.");
        }
        
        cachingController = new CachingController();
        cachingController.init(initAttributes);
        
        return cachingController;
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
