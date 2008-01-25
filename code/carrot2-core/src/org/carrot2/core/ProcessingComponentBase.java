package org.carrot2.core;

/**
 * A base class for implementation of the {@link ProcessingComponent} interface that
 * provides empty implementations of all life cycle methods.
 */
public abstract class ProcessingComponentBase implements ProcessingComponent
{
    @Override
    public void init()
    {
    }

    @Override
    public void beforeProcessing() throws ProcessingException
    {
    }

    @Override
    public void process() throws ProcessingException
    {
    }

    @Override
    public void afterProcessing()
    {
    }

    @Override
    public void dispose()
    {
    }
}