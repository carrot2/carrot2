package org.carrot2.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.carrot2.core.parameters.Bindable;
import org.carrot2.core.parameters.Parameter;
import org.carrot2.core.parameters.BindingPolicy;


@Bindable
public class TestClusteringAlgorithm implements ClusteringAlgorithm
{
    @Parameter(policy=BindingPolicy.RUNTIME)
    Collection<Document> documents;

    @Override
    public Iterator<Cluster> getClusters()
    {
        return Collections.<Cluster>emptyList().iterator();
    }
}
