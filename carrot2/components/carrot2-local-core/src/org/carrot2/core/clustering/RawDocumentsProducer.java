
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

package org.carrot2.core.clustering;

/**
 * A marker interface for components that can produce {@link RawDocument}
 * objects.
 * 
 * <p>
 * Successor components to this one should implement the corresponding {@link
 * RawDocumentsConsumer} interface.
 * </p>
 * 
 * @author Dawid Weiss
 * @version $Revision$
 * 
 * @see RawDocument
 * @see RawDocumentsConsumer
 * @see org.carrot2.core.LocalComponent#getComponentCapabilities()
 * @see org.carrot2.core.LocalComponent
 */
public interface RawDocumentsProducer
{
    /**
     * Producers which have access to the original partitioning of the input
     * data, may use this property to provide a {@link String}representing the
     * unique identifier of the document's original partition id.
     */
    public static final String PROPERTY_CATID = "catid";

    /**
     * Producers which have access to the original partitioning of the input
     * data, may set this property to a {@link java.util.List}of
     * {@link RawCluster}instances corresponding to this partitioning. Each of
     * the original clusters must also have a non- <code>null</code> property
     * {@link #PROPERTY_CATID}with the unique partition identifier.
     */
    public static final String PARAM_ORIGINAL_RAW_CLUSTERS = "orig-raw-clusters";
}