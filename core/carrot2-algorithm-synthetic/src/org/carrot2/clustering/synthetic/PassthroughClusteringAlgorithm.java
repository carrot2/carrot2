
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.synthetic;

import java.util.List;

import org.carrot2.core.Cluster;
import org.carrot2.core.IClusteringAlgorithm;
import org.carrot2.core.ProcessingComponentBase;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.CommonAttributes;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Output;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * A do-nothing implementation of {@link IClusteringAlgorithm}. If no clusters are
 * provided from predecessor components, it produces an empty set of clusters. Otherwise
 * it just passes through the input cluster set.
 */
@Bindable(prefix = "PassthroughClusteringAlgorithm", inherit = CommonAttributes.class)
@Label("By Attribute Clustering")
public class PassthroughClusteringAlgorithm extends ProcessingComponentBase implements IClusteringAlgorithm
{
    /**
     * Any clusters already provided by the predecessor components. If null,
     * an empty array will be provided.
     */
    @Processing
    @Output
    @Input
    @Internal
    @Attribute(key = AttributeNames.CLUSTERS, inherit = true)
    public List<Cluster> clusters;

    /**
     * Performs by URL clustering.
     */
    @Override
    public void process() throws ProcessingException
    {
        if (clusters == null) {
            clusters = Lists.newArrayList();
        }
    }
}
