
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2014, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples.source;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * An example clustering algorithm component that groups documents
 * by the first letter of their title.
 */
@Bindable(prefix = "ByFirstLetter", inherit = CommonAttributes.class)
public class ByFirstTitleLetterClusteringAlgorithm 
    extends ProcessingComponentBase 
    implements IClusteringAlgorithm
{
    /**
     * Documents to cluster.
     */
    @Processing
    @Input
    @Internal
    @Attribute(key = AttributeNames.DOCUMENTS, inherit = true)
    public List<Document> documents;

    /**
     * Clusters created by the algorithm.
     */
    @Processing
    @Output
    @Internal
    @Attribute(key = AttributeNames.CLUSTERS, inherit = true)
    public List<Cluster> clusters = null;

    /**
     * Whether to group case-insensitive codepoints together.
     */
    @Init
    @Processing
    @Input
    @Attribute
    public boolean caseSensitive = true;

    /**
     * Do the processing.
     */
    @Override
    public void process() throws ProcessingException
    {
        final Map<Integer, Cluster> codepointToCluster = Maps.newHashMap();
        for (Document document : documents)
        {
            final String title = document.getTitle();
            Integer codepoint;
            if (title == null || title.trim().isEmpty())
            {
                // No letter in the title -- will become other topics.
                continue;
            }
            else
            {
                codepoint = title.trim().codePointAt(0);
            }
            
            if (!caseSensitive)
            {
                // This is overly simplistic, but will do for the example.
                codepoint = Character.toLowerCase(codepoint);
            }

            if (!codepointToCluster.containsKey(codepoint))
            {
                codepointToCluster.put(codepoint, 
                    new Cluster("Starting with letter '" 
                        + new String(Character.toChars(codepoint)) + "'"));
            }

            codepointToCluster.get(codepoint).addDocuments(document);
        }

        clusters = Lists.newArrayList(codepointToCluster.values());
        Collections.sort(clusters, Cluster.BY_REVERSED_SIZE_AND_LABEL_COMPARATOR);
        Cluster.appendOtherTopics(documents, clusters);        
    }
}
