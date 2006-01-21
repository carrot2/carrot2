
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

package carrot2.demo.cache;

import java.io.IOException;
import java.util.*;

import org.apache.lucene.store.Directory;

import carrot2.demo.index.RawDocumentsLuceneIndexBuilder;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.impl.ClustersConsumerOutputComponent;
import com.dawidweiss.carrot.core.local.impl.ClustersConsumerOutputComponent.Result;

/**
 * A simple input component caching results from a
 * {@link com.dawidweiss.carrot.core.local.clustering.RawDocumentsProducer}
 * and reusing it in subsequent requests.
 * 
 * <b>This component can be considered a hack, so I wouldn't use it anywhere outside of the browser
 * component.</b> The cached request is remembered as a pair of {wrapped-component, query}. The request
 * context is important only at the time the first request is made (and cached). All subsequent requests
 * acquired from the cache do not rely on the request context parameters (which might affect the
 * input component's response if cache were not used).
 * 
 * @author Dawid Weiss
 */
public class RawDocumentProducerCacheWrapper extends LocalInputComponentBase {

    /** */
    public static final String PARAM_INDEX_CONTENT = "index-content";
    
    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = new HashSet(
            Arrays.asList(new Object [] { RawDocumentsConsumer.class }));

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = new HashSet(
            Arrays.asList(new Object [] { RawDocumentsProducer.class }));

    /**
     * Static shared cache of query results.
     */
    private final static RawDocumentsCache cache = 
        new WeakRawDocumentsCache(/* default hard cache size */ 3);

    /** The wrapped component. */
    private final LocalInputComponent wrapped;

    /** Current query. */
    private String query;
    
    /** <code>true</code> if caching the wrapped delegate is in progress at the moment. */
    private boolean currentlyCaching;

    /** Current request context */
    private RequestContext requestContext;
    
    /**
     * Consumer for {@link com.dawidweiss.carrot.core.local.clustering.RawDocument}s.
     */
    private ClustersConsumerOutputComponent consumer; 
	private Result cachedResult;

    public RawDocumentProducerCacheWrapper(LocalInputComponent wrappedComponent) {
        // Make sure the wrapped component implements
        // the required capability.
        final Set caps = wrappedComponent.getComponentCapabilities();
        if (false == caps.contains(RawDocumentsProducer.class)) {
            throw new IllegalArgumentException("Wrapped component does not expose the required capability: "
                    + RawDocumentsProducer.class);
        }
        this.wrapped = wrappedComponent;
        this.currentlyCaching = false;
    }

    public Set getRequiredSuccessorCapabilities() {
        return SUCCESSOR_CAPABILITIES; 
    }

    public Set getRequiredPredecessorCapabilities() {
        return java.util.Collections.EMPTY_SET;
    }
    
    public Set getComponentCapabilities() {
        return COMPONENT_CAPABILITIES;
    }
    
    public void startProcessing(RequestContext requestContext) throws ProcessingException {
    	super.startProcessing(requestContext);
    	this.requestContext = requestContext;
        int requestedResults = 0;
        final Object requestedResultsParam = requestContext.getRequestParameters().get(
            LocalInputComponent.PARAM_REQUESTED_RESULTS);
        if (requestedResultsParam != null)
        {
            requestedResults = Integer.parseInt(requestedResultsParam.toString());
        }
        
        final ClustersConsumerOutputComponent.Result result
            = cache.get(new CacheEntry(wrapped, query, requestedResults));

        if (result == null) {
            // Requires caching.
            this.currentlyCaching = true;

            this.consumer = new ClustersConsumerOutputComponent();
            wrapped.setNext(consumer);
            wrapped.setQuery(query);
            wrapped.startProcessing(requestContext);
            this.cachedResult = null;
        } else {
            // Already cached.
            this.cachedResult = result;
            this.currentlyCaching = false;
        }
    }

    public void endProcessing() throws ProcessingException {
        if (currentlyCaching) {
            // Caching in progress, finish it.
            this.wrapped.endProcessing();

            // Ok, save the result 
            final ClustersConsumerOutputComponent.Result result = 
                (ClustersConsumerOutputComponent.Result) this.consumer.getResult();

            this.cachedResult = result;

            // Build Lucene index
            buildLuceneIndex(result);

            int requestedResults = 0;
            final Object requestedResultsParam = requestContext.getRequestParameters().get(
                LocalInputComponent.PARAM_REQUESTED_RESULTS);
            if (requestedResultsParam != null)
            {
                requestedResults = Integer.parseInt(requestedResultsParam.toString());
            }

            cache.put(new CacheEntry(wrapped, query, requestedResults), this.cachedResult);
        }

        requestContext.getRequestParameters().putAll(this.cachedResult.context);
        
        // Playback RawDocuments from cache.
        final RawDocumentsConsumer nextComponent = (RawDocumentsConsumer) super.next; 
        for (Iterator i = this.cachedResult.documents.iterator(); i.hasNext();) {
            final RawDocument doc = (RawDocument) i.next();
            nextComponent.addDocument(doc);
        }

        super.endProcessing();
    }

    /**
     * @param result
     * @throws ProcessingException
     */
    private void buildLuceneIndex(final ClustersConsumerOutputComponent.Result result) throws ProcessingException {
        try {
            Directory luceneIndex = RawDocumentsLuceneIndexBuilder.index(result.documents);
            result.context.put(PARAM_INDEX_CONTENT, luceneIndex);
        }
        catch (IOException e) {
            throw new ProcessingException("Error while building Lucene index", e);
        }
    }

    public void flushResources() {
        if (currentlyCaching) {
            this.wrapped.flushResources();
        }

        this.query = null;
        this.cachedResult = null;
        this.currentlyCaching = false;
        this.requestContext = null;

        super.flushResources();
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void processingErrorOccurred() {
        if (currentlyCaching) {
            this.wrapped.processingErrorOccurred();
        }

        super.processingErrorOccurred();
    }
}