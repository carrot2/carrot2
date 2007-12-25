package org.carrot2.core;

import org.carrot2.core.parameter.*;

@Bindable
public class TestDocumentSource implements DocumentSource
{
    @Parameter(policy = BindingPolicy.RUNTIME)
    int numDocs;

    @Parameter(policy = BindingPolicy.RUNTIME)
    String query;

    @Override
    public void afterProcessing()
    {
    }

    @Override
    public void beforeProcessing() throws ProcessingException
    {
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void init() throws InitializationException
    {
    }

    @Override
    public void performProcessing() throws ProcessingException
    {
    }
}
