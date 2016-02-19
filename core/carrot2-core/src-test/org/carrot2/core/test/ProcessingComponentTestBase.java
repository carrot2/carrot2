
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

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.After;
import org.junit.Before;

/**
 * A base class for testing {@link IProcessingComponent}s. This class provides
 * simple and caching controller along with maps for attributes and a method that
 * initializes them.
 */
public abstract class ProcessingComponentTestBase<T extends IProcessingComponent> 
    extends CarrotTestCase
{
    /** Simple controller used for tests. */
    protected Controller simpleController;

    /** Caching controller used for tests. */
    protected Controller cachingController;

    /** A map of initialization attributes used for tests. */
    protected Map<String, Object> initAttributes;

    /** A map of processing attributes used for tests. */
    protected Map<String, Object> processingAttributes;

    /** A map of processing attributes used for tests. */
    protected Map<String, Object> resultAttributes;

    /**
     * @return Return the class of the component being tested.
     */
    public abstract Class<T> getComponentClass();

    /**
     * Controller and attributes are cleared before every test.
     */
    @Before
    private void prepareComponent()
    {
        this.initAttributes = new HashMap<String, Object>();
        this.processingAttributes = new HashMap<String, Object>();
    }

    /**
     * Cleanup.
     */
    @After
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
     * Return an instance of a simple {@link Controller}, initializing it on the way.
     */
    protected final Controller getSimpleController(Map<String, Object> initAttributes)
    {
        if (this.simpleController != null)
        {
            throw new RuntimeException("One simple controller per test case, please.");
        }

        simpleController = ControllerFactory.createSimple();
        simpleController.init(initAttributes);

        return simpleController;
    }

    /**
     * Return an instance of a {@link Controller} with caching, initializing it on the way.
     */
    @SuppressWarnings("unchecked")
    protected final Controller getCachingController(Map<String, Object> initAttributes,
        Class<? extends IProcessingComponent>... cachedComponentClasses)
    {
        if (this.cachingController != null)
        {
            throw new RuntimeException("One caching controller per test case, please.");
        }

        cachingController = ControllerFactory
            .createCachingPooling(cachedComponentClasses);
        cachingController.init(initAttributes);

        return cachingController;
    }

    /**
     * Returns the documents stored in {@link #resultAttributes}.
     */
    @SuppressWarnings("unchecked")
    protected List<Document> getDocuments()
    {
        return (List<Document>) resultAttributes.get(AttributeNames.DOCUMENTS);
    }
}
