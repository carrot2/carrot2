
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

package com.stachoodev.carrot.filter.lingo.local;

import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.*;
import com.dawidweiss.carrot.core.local.profiling.*;
import com.stachoodev.carrot.filter.lingo.common.*;
import com.stachoodev.carrot.filter.lingo.common.Cluster;
import com.stachoodev.carrot.filter.lingo.common.MultilingualClusteringContext;


/**
 * An adapter for the 'old' Lingo to the new local interfaces
 * architecture. The old lingo is still in use, so it makes sense to 
 * add this support.
 * 
 * @author Dawid Weiss
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LingoLocalFilterComponent extends ProfiledLocalFilterComponentBase
    implements RawDocumentsConsumer, RawClustersProducer, RawDocumentsProducer, LocalFilterComponent
{
    /**
     * If this parameter is not-null (e.g. Boolean.TRUE), Lingo will leave in
     * the {@link RequestContext} an array of {@link Feature}s found in the
     * input snippets. Use the {@link #LINGO_EXTRACTED_FEATURES} key to obtain
     * the features from the context.
     */
    public static final String PARAMETER_LEAVE_FEATURES_IN_CONTEXT = "leave-features";

    /**
     * When the {@link #PARAMETER_LEAVE_FEATURES_IN_CONTEXT} request parameter
     * is not-null, Lingo will leave under this key in the
     * {@link RequestContext} an array of {@link Feature}s extracted while 
     * clustering.
     */
    public static final String LINGO_EXTRACTED_FEATURES = "lingo-extracted-features";
    
    /** Documents to be clustered */
    private ArrayList documents;

    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object [] { RawDocumentsProducer.class }));

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object [] { RawDocumentsConsumer.class, RawClustersProducer.class }));

    /** Capabilities required from the next component in the chain */
    private final static Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays
        .asList(new Object [] { RawClustersConsumer.class }));

    /** Raw clusters consumer */
    private RawClustersConsumer rawClustersConsumer;
    
    /** Raw documents consumer */
    private RawDocumentsConsumer rawDocumentsConsumer;
    
    /**
     *  Request params.
     */
    private Map requestParams;

    /**
     * An array of languages "recognized" by Lingo and used for preprocessing.
     */
	private Language[] languages;

	/**
	 * An array of default algorithm settings.
	 */
	private Map defaults;

    /**
     * Creates a new Lingo filter with default parameters.
     */
    public LingoLocalFilterComponent()
    {
    	this(null, null);
    }
    
    public LingoLocalFilterComponent(Language [] languages, Map parameters) {
    	this.languages = languages;
    	this.defaults = parameters;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#init(com.dawidweiss.carrot.core.local.LocalControllerContext)
     */
    public void init(LocalControllerContext context)
        throws InstantiationException
    {
        super.init(context);
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalFilterComponent#setNext(com.dawidweiss.carrot.core.local.LocalComponent)
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        rawClustersConsumer = (RawClustersConsumer) next;
        if (next instanceof RawDocumentsConsumer) {
            rawDocumentsConsumer = (RawDocumentsConsumer) next;
        }
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();

        documents = null;
        rawClustersConsumer = null;
        rawDocumentsConsumer = null;
        requestParams = null;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#startProcessing(com.dawidweiss.carrot.core.local.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        super.startProcessing(requestContext);
        this.requestParams = requestContext.getRequestParameters();
        documents = new ArrayList();        
    }

    /*
     * @see com.dawidweiss.carrot.core.local.clustering.TokenizedDocumentsConsumer#addDocument(com.dawidweiss.carrot.core.local.clustering.TokenizedDocument)
     */
    public void addDocument(RawDocument doc) throws ProcessingException
    {
        documents.add( new SnippetInterfaceAdapter(Integer.toString(documents.size()), doc));
        if (rawDocumentsConsumer != null) {
            rawDocumentsConsumer.addDocument(doc);
        }
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#endProcessing()
     */
    public void endProcessing() throws ProcessingException
    {
        // Unfortunately, Lingo does not support incremental processing, so we
        // need to wait for all documents to come before we start processing
        startTimer();

        // Prepare data
        MultilingualClusteringContext clusteringContext = new MultilingualClusteringContext(new HashMap());
        
        final Language [] languages = this.languages;
        
        // set the default parameters of the algorithm.
        // eh.. quick and dirty as always.
        Map current = new HashMap();
        if (this.defaults != null)
        	current.putAll(this.defaults);
        current.putAll(requestParams);
        clusteringContext.setParameters(current);
        clusteringContext.setLanguages(languages);

        if (documents.size() == 0) {
        	super.endProcessing();
        	return;
        }

        // Add documents to cluster.
        for (Iterator i = documents.iterator(); i.hasNext(); ) {
        	clusteringContext.addSnippet((SnippetInterfaceAdapter) i.next() );
        }

        // Query
        String query = (String) requestParams.get(LocalInputComponent.PARAM_QUERY);
        clusteringContext.setQuery(query);

        // Cluster them now.
        Cluster[] clusters = clusteringContext.cluster();
        
        // convert (lazily) back to the interfaces required by local architecture.
        for (int i = 0; i < clusters.length; i++) {
            this.rawClustersConsumer.addCluster(
            		new RawClusterInterfaceAdapter( clusters[i], documents ));
        }

        // Copy features to the context parameters
        requestParams.put(LINGO_EXTRACTED_FEATURES, current
            .get(LINGO_EXTRACTED_FEATURES));
        
        stopTimer();

        super.endProcessing();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getName()
     */
    public String getName()
    {
        return "Lingo (old)";
    }
}