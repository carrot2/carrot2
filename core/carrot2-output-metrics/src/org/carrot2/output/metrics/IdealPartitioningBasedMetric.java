
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

package org.carrot2.output.metrics;

import java.util.*;

import org.carrot2.core.Document;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;

import org.carrot2.shaded.guava.common.base.Function;
import org.carrot2.shaded.guava.common.collect.*;

/**
 * A base class for metrics based on some reference partitioning.
 */
@Bindable
public abstract class IdealPartitioningBasedMetric implements IClusteringMetric
{
    /**
     * Partition id field name.
     */
    @Input
    @Processing
    @Attribute
    public String partitionIdFieldName = Document.PARTITIONS;

    Set<Object> getPartitions(List<Document> documents)
    {
        final HashSet<Object> partitions = Sets.newHashSet();
        for (Document document : documents)
        {
            final Collection<Object> documentPartitions = document
                .<Collection<Object>> getField(partitionIdFieldName);
            if (documentPartitions != null)
            {
                partitions.addAll(documentPartitions);
            }
        }
        return partitions;
    }

    /**
     * Returns the number of distinct {@link Document#PARTITIONS}s in a collection of
     * documents. Note if that at least one of the document has a <code>null</code>
     * partition, 0 will be returned.
     */
    int getPartitionsCount(List<Document> documents)
    {
        return getPartitions(documents).size();
    }

    /**
     * Returns documents grouped by partitions.
     */
    SetMultimap<Object, Document> getDocumentsByPartition(List<Document> documents)
    {
        final SetMultimap<Object, Document> index = HashMultimap.create();
        for (Document document : documents)
        {
            final Collection<Object> partitions = document.getField(partitionIdFieldName);
            for (Object partition : partitions)
            {
                index.put(partition, document);
            }
        }

        return ImmutableSetMultimap.copyOf(index);
    }

    /**
     * Returns document counts for each partition.
     */
    Map<Object, Integer> getDocumentCountByPartition(List<Document> documents)
    {
        return ImmutableMap.copyOf(Maps.transformValues(
            getDocumentsByPartition(documents).asMap(),
            new Function<Collection<Document>, Integer>()
            {
                public Integer apply(Collection<Document> documents)
                {
                    return documents.size();
                }
            }));
    }
}
