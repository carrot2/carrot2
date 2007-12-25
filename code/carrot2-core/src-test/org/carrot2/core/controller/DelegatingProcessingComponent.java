/**
 * 
 */
package org.carrot2.core.controller;

import org.carrot2.core.*;
import org.carrot2.core.parameter.*;

@Bindable
public abstract class DelegatingProcessingComponent implements ProcessingComponent
{
    @Parameter(policy = BindingPolicy.INSTANTIATION)
    private String instanceParameter;

    @Parameter(policy = BindingPolicy.RUNTIME)
    private String runtimeParameter;
    
    @SuppressWarnings("unused")
    @Attribute(key = "data", bindingDirection = BindingDirection.INOUT)
    private String data;

    @Override
    public void init() throws InitializationException
    {
        getDelegate().init();
    }

    @Override
    public void beforeProcessing() throws ProcessingException
    {
        getDelegate().beforeProcessing();
    }

    @Override
    public void performProcessing() throws ProcessingException
    {
        getDelegate().performProcessing();

        // Do some simple processing
        data += instanceParameter + runtimeParameter;
    }

    @Override
    public void afterProcessing()
    {
        getDelegate().afterProcessing();
    }

    @Override
    public void dispose()
    {
        getDelegate().dispose();
    }

    abstract ProcessingComponent getDelegate();
}
