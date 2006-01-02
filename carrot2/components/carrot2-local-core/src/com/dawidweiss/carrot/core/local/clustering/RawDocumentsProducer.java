
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.core.local.clustering;

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
 * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
 * @see com.dawidweiss.carrot.core.local.LocalComponent
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