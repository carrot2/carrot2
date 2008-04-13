/**
 *
 */
package org.carrot2.core;

import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
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

    @Processing
    @Input
    @Output
    @Required
    @Attribute(key = "data")
    private String data = null;

    public void init()
    {
        // We perform synchronization here because the mock object passed as the delegate
        // is not thread-safe and may fail to record some invocations if not synchronized
        synchronized (getDelegate())
        {
            getDelegate().init();
        }
    }

    public void beforeProcessing() throws ProcessingException
    {
        // We perform synchronization here because the mock object passed as the delegate
        // is not thread-safe and may fail to record some invocations if not synchronized
        synchronized (getDelegate())
        {
            getDelegate().beforeProcessing();
        }
    }

    public void process() throws ProcessingException
    {
        // We perform synchronization here because the mock object passed as the delegate
        // is not thread-safe and may fail to record some invocations if not synchronized
        synchronized (getDelegate())
        {
            getDelegate().process();
        }

        // Do some simple processing
        data = data + instanceAttribute;
        data = data + runtimeAttribute;
    }

    public void afterProcessing()
    {
        // We perform synchronization here because the mock object passed as the delegate
        // is not thread-safe and may fail to record some invocations if not synchronized
        synchronized (getDelegate())
        {
            getDelegate().afterProcessing();
        }
    }

    public void dispose()
    {
        // We perform synchronization here because the mock object passed as the delegate
        // is not thread-safe and may fail to record some invocations if not synchronized
        synchronized (getDelegate())
        {
            getDelegate().dispose();
        }
    }

    abstract ProcessingComponent getDelegate();
}
