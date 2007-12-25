/**
 * 
 */
package org.carrot2.core;

import org.carrot2.core.parameter.*;

@Bindable
public class DelegatingProcessingComponent implements ProcessingComponent
{
    @Parameter(policy = BindingPolicy.INSTANTIATION)
    private ProcessingComponent delegate;

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
        delegate.init();
    }

    @Override
    public void beforeProcessing() throws ProcessingException
    {
        delegate.beforeProcessing();
    }

    @Override
    public void performProcessing() throws ProcessingException
    {
        delegate.performProcessing();

        // Do some simple processing
        data += instanceParameter + runtimeParameter;
    }

    @Override
    public void afterProcessing()
    {
        delegate.afterProcessing();
    }

    @Override
    public void dispose()
    {
        delegate.dispose();
    }
}
