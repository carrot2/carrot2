package org.carrot2.core;

/**
 * A base class for implementation of the {@link ProcessingComponent} interface that
 * provides empty implementations of all life cycle methods.
 */
public abstract class ProcessingComponentBase implements ProcessingComponent
{
    /*
     * 
     */
    public void init()
    {
    }

    /*
     * 
     */
    public void beforeProcessing() throws ProcessingException
    {
    }

    /*
     * 
     */
    public void process() throws ProcessingException
    {
    }

    /*
     * 
     */
    public void afterProcessing()
    {
    }

    /*
     * 
     */
    public void dispose()
    {
    }
}