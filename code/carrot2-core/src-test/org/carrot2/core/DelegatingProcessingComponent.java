/**
 * 
 */
package org.carrot2.core;

import org.carrot2.core.attribute.*;

@Bindable
public abstract class DelegatingProcessingComponent implements ProcessingComponent
{
    @Init
    @Input
    @Attribute(key = "instanceAttribute")
    private String instanceAttribute = "";

    @Processing
    @Input
    @Attribute(key = "runtimeAttribute")
    private String runtimeAttribute = "";

    @SuppressWarnings("unused")
    @Processing
    @Input
    @Output
    @Attribute(key = "data")
    private String data;

    @Override
    public void init()
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
        data += instanceAttribute + runtimeAttribute;
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
