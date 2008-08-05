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
