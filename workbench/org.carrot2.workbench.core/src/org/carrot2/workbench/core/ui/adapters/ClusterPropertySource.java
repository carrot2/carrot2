
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui.adapters;

import java.util.Map;

import org.carrot2.core.Cluster;

import com.google.common.collect.Maps;

public final class ClusterPropertySource extends MapPropertySource
{
    public ClusterPropertySource(Cluster cluster)
    {
        final Map<String, Object> properties = Maps.newHashMap();
        
        properties.put("ID", cluster.getId());
        properties.put("label", cluster.getLabel());

        if (!cluster.getDocuments().isEmpty())
        {
            properties.put("documents", cluster.getDocuments());
        }

        if (!cluster.getSubclusters().isEmpty())
        {
            properties.put("sub-clusters", cluster.getSubclusters());
        }

        if (!cluster.getAttributes().isEmpty())
        {
            properties.put("attributes", cluster.getAttributes());
        }

        add(properties, null);
    }
}
