
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package carrot2.demo;

import com.dawidweiss.carrot.core.local.clustering.RawCluster;

/**
 * Classes implementing this interface provide additional information about a
 * {@link RawCluster}, e.g. scores, flags etc.
 * 
 * @author Stanislaw Osinski
 */
public interface ClusterInfoRenderer
{
    /**
     * Returns a prefix to be rendered before the provided cluster's label.
     * 
     * @param rawCluster
     * @return
     */
    public String getClusterLabelPrefix(RawCluster rawCluster);

    /**
     * Returns a suffix to be rendered after the provided cluster's label.
     * 
     * @param rawCluster
     * @return
     */
    public String getClusterLabelSuffix(RawCluster rawCluster);

    /**
     * Returns additional information about the provided cluster in plain text
     * format.
     * 
     * @param rawCluster
     * @return
     */
    public String getPlainClusterInfo(RawCluster rawCluster);

    /**
     * Returns additional information about the provided cluster in HTML format.
     * (html and body tags must not be returned -- they will be added by the
     * caller).
     * 
     * @param rawCluster
     * @return
     */
    public String getHtmlClusterInfo(RawCluster rawCluster);
}