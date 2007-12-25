package org.carrot2.core;

import java.util.Collection;

import org.carrot2.core.parameter.*;


@Bindable
public class TestClusteringAlgorithm implements ClusteringAlgorithm
{
    @Parameter(policy=BindingPolicy.RUNTIME)
    Collection<Document> documents;

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
