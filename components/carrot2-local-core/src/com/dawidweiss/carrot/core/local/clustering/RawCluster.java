
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

package com.dawidweiss.carrot.core.local.clustering;

import java.util.List;

/**
 * A cluster object holds a list of {@link RawDocument}references and possibly
 * a list of sub-clusters.
 * 
 * <p>
 * Classes marked with this interface form clusers of <i>raw </i> documents.
 * <b>The raw interfaces should be gradually refactor to use
 * <code>carrot2-tokenizer</code> component </b>.
 * </p>
 * 
 * <p>
 * A cluster may have additional name-value properties available via {@link
 * #getProperty(String)} method.
 * </p>
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public interface RawCluster
{
    /**
     * Score of this cluster, if available. The value of this property must be
     * an instance of {@link Double}.
     */
    public final static String PROPERTY_SCORE = "score";
    
    /**
     * Algorithms may set this property to a non- <code>null</code> value for
     * a cluster to suggest that the cluster contains e.g. unrelated or
     * unassigned documents. Presentation layer can use this information to
     * suppress displaying such clusters and cluster metrics can exclude them
     * from assessment.
     */
    public final static String PROPERTY_JUNK_CLUSTER = "junk";

    /**
     * Returns phrases denoting this cluster's description. The order of phrases
     * reflects their relevance to being a good cluster description - if there
     * is a limit of phrases an application can display, phrases from the end of
     * the list should be omitted.
     * 
     * @return A list of <code>String</code> objects representing phrases. The
     *         list can be empty, but is never <code>null</code>.
     */
    public List getClusterDescription();

    /**
     * @return Returns a list of sub-clusters, each of type {@link RawCluster}.
     */
    public List getSubclusters();

    /**
     * @return Returns a list of documents in this cluster, each of type {@link
     *         RawDocument}.
     */
    public List getDocuments();

    /**
     * Returns a named property of this cluster.
     * 
     * @param propertyName Name of the property to retrieve.
     * 
     * @return The value of the property or <code>null</code> if this property
     *         is not available for this cluster.
     */
    public Object getProperty(String propertyName);

    /**
     * Sets a value for a named property in this cluster.
     * 
     * @param propertyName Name of the property to set.
     * @param value The new value of the property.
     * 
     * @return Previous value of the property if it existed, or
     *         <code>null</code>.
     */
    public Object setProperty(String propertyName, Object value);
}