
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 *
 * Sponsored by: CCG, Inc.
 */

package com.dawidweiss.carrot.core.local.clustering;

import java.util.List;


/**
 * A cluster object holds a list of  {@link TokenizedDocument} references and
 * possibly a list of sub-clusters.
 * 
 * <p>
 * Classes marked with this interface form clusers of <i>tokenized</i>
 * documents. See  <code>carrot2-tokenizer</code>   component for details.
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
public interface Cluster {
    /**
     * Returns phrases denoting this cluster's description. The order of
     * phrases reflects their relevance to being a good cluster description -
     * if there is a limit of phrases an application can display, phrases from
     * the end of the list should be omitted.
     *
     * @return A list of description phrases, each of type {@link
     *         com.dawidweiss.carrot.core.local.linguistic.tokens.TokenSequence}.
     */
    public List getClusterDescription();

    /**
     * @return Returns a list of sub-clusters, each of type {@link Cluster}.
     */
    public List getSubclusters();

    /**
     * @return Returns a list of documents in this cluster, each of type {@link
     *         TokenizedDocument}.
     */
    public List getDocuments();

    /**
     * Returns a named property of this cluster.
     *
     * @param propertyName Name of the property to retrieve.
     *
     * @return The value of the property or <code>null</code>  if this property
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
