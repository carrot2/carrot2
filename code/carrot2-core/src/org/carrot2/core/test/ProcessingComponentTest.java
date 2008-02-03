/**
 * 
 */
package org.carrot2.core.test;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.core.controller.SimpleController;
import org.junit.Before;

/**
 * A very basic base class for testing {@link ProcessingComponent}s. This class provides
 * an instance of {@link SimpleController}, a map for attributes and a method that
 * initializes them.
 */
public abstract class ProcessingComponentTest<T extends ProcessingComponent>
{
    /** Controller used for tests. */
    protected SimpleController controller;

    /** A map of attributes used for tests. */
    protected Map<String, Object> attributes;

    /**
     * @return Return the class of the component being tested.
     */
    public abstract Class<T> getComponentClass();

    /**
     * Controller and attributes are cleared before every test.
     */
    @Before
    public void prepareComponent()
    {
        this.controller = new SimpleController();
        this.attributes = new HashMap<String, Object>();
    }
}
