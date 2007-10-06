/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.impl;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;

/**
 * An output component that can consume {@link RawDocument}s or {@link RawCluster}s but
 * does not do any processing on them. This component might be useful as a terminator of a
 * processing chaing where it is not important to capture the result or the result has
 * already been captured, eg. by the {@link SaveXmlFilterComponent}.
 * 
 * @author Stanislaw Osinski
 * @version $Revision: 1539 $
 */
public class NullOutputComponent extends LocalOutputComponentBase implements
    LocalOutputComponent, RawClustersConsumer, RawDocumentsConsumer
{
    /**
     * A shared instance of the component. 
     */
    public final static NullOutputComponent INSTANCE = new NullOutputComponent();
    
    /**
     * Capabilities exposed by this component.
     */
    private static final Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object []
        {
            RawClustersConsumer.class, RawDocumentsConsumer.class
        }));

    /**
     * Capabilities required of the predecessor component. No specific requirements are
     * needed (we can use this component together with {@link RawDocumentsProducer}s or
     * {@link RawClustersProducer}s.
     */
    private static final Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object [] {}));

    public void startProcessing(RequestContext requestContext) throws ProcessingException
    {
    }

    /**
     * Provides an empty implementation
     */
    public void endProcessing() throws ProcessingException
    {
    }

    /**
     * Adds a cluster to the list of clusters to be returned as the result.
     */
    public void addCluster(RawCluster cluster) throws ProcessingException
    {
    }

    /**
     * Adds a document to the list of documents to be returned as the result.
     */
    public void addDocument(RawDocument doc) throws ProcessingException
    {
    }

    /**
     * Clears clusters and documents lists and prepares the component for reuse.
     */
    public void flushResources()
    {
        super.flushResources();
    }

    /**
     * @see org.carrot2.core.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    /**
     * @see org.carrot2.core.LocalComponent#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    /**
     * Returns <code>null</code>.
     */
    public Object getResult()
    {
        return null;
    }
}
