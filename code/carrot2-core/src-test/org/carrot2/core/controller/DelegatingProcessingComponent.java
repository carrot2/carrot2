/**
 * 
 */
package org.carrot2.core.controller;

import org.carrot2.core.*;
import org.carrot2.core.parameter.*;

@Bindable
public abstract class DelegatingProcessingComponent implements ProcessingComponent
{
    @Init
    @Input
    @Attribute(key = "instanceParameter")
    private String instanceParameter = "";

    @Processing
    @Input
    @Attribute(key = "runtimeParameter")
    private String runtimeParameter = "";

    @SuppressWarnings("unused")
    @Processing
    @Input
    @Output
    @Attribute(key = "data")
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
    public void process() throws ProcessingException
    {
        getDelegate().process();

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
