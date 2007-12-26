package org.carrot2.core;

/**
 * A base class for implementation of the {@link ProcessingComponent} interface that
 * provides empty implementations of all life cycle methods.
 */
public class ProcessingComponentBase implements ProcessingComponent
{
    @Override
    public void init() throws InitializationException
    {
    }

    @Override
    public void beforeProcessing() throws ProcessingException
    {
    }

    @Override
    public void performProcessing() throws ProcessingException
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