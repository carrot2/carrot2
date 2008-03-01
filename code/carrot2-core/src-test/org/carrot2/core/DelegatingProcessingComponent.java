/**
 *
 */
package org.carrot2.core;

import org.carrot2.util.attribute.*;

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

    public void init()
    {
        getDelegate().init();
    }

    public void beforeProcessing() throws ProcessingException
    {
        getDelegate().beforeProcessing();
    }

    public void process() throws ProcessingException
    {
        getDelegate().process();

        // Do some simple processing
        data = data + instanceAttribute;
        data = data + runtimeAttribute;
    }

    public void afterProcessing()
    {
        getDelegate().afterProcessing();
    }

    public void dispose()
    {
        getDelegate().dispose();
    }

    abstract ProcessingComponent getDelegate();
}
