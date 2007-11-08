package org.carrot2.util.resources;

/**
 * An abstraction layer for resource lookup. 
 * 
 * @see ResourceUtils
 */
public interface ResourceLocator
{
    /**
     * Locate All matching resources and return their abstraction. 
     */
    public Resource [] getAll(String resource, Class clazz);
}
