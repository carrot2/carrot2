
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

package org.carrot2.demo.cache;

import java.io.IOException;
import java.util.*;

import org.apache.lucene.store.Directory;
import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.impl.ArrayOutputComponent;
import org.carrot2.core.impl.ArrayOutputComponent.Result;
import org.carrot2.demo.index.RawDocumentsLuceneIndexBuilder;

/**
 * A simple input component caching results from a
 * {@link org.carrot2.core.clustering.RawDocumentsProducer}
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

    /** Wrapped component's equivalence class identifier. */
    private Object equivalenceClass;

    /** Current query. */
    private String query;
    
    /** <code>true</code> if caching the wrapped delegate is in progress at the moment. */
    private boolean currentlyCaching;

    /** Current request context */
    private RequestContext requestContext;
    
    /**
     * Consumer for {@link org.carrot2.core.clustering.RawDocument}s.
     */
    private ArrayOutputComponent consumer; 
	private Result cachedResult;


    /**
     * Creates a wrapper around a <code>wrappedComponent</code> with an equivalence class identified
     * by <code>wrappedComponentClassId</code>
     * 
     * @param wrappedComponent Wrapped input component instance.
     * @param wrappedComponentClassId Any object which implies an equivalence class between cached results from the
     *      same component. We need this token because the wrapped component instance can change between subsequent
     *      requests (same component factory, but different instances) and the input component's class may not be
     *      enough (customizations such as service URL in the constructor).
     */
    public RawDocumentProducerCacheWrapper(LocalInputComponent wrappedComponent, Object wrappedComponentClassId) {
        // Make sure the wrapped component implements
        // the required capability.
        final Set caps = wrappedComponent.getComponentCapabilities();
        if (false == caps.contains(RawDocumentsProducer.class)) {
            throw new IllegalArgumentException("Wrapped component does not expose the required capability: "
                    + RawDocumentsProducer.class);
        }
        this.wrapped = wrappedComponent;
        this.equivalenceClass = wrappedComponentClassId;
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
        
        final ArrayOutputComponent.Result result
            = cache.get(new CacheEntry(wrapped, equivalenceClass, query, requestedResults));

        if (result == null) {
            // Requires caching.
            this.currentlyCaching = true;

            this.consumer = new ArrayOutputComponent();
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
            final ArrayOutputComponent.Result result = 
                (ArrayOutputComponent.Result) this.consumer.getResult();

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

            cache.put(new CacheEntry(wrapped, equivalenceClass, query, requestedResults), this.cachedResult);
        }

        // Copy cached context parameters over to the current context parameters
        // We copy only these parameters which are not present in the current
        // request context. Otherwise live update doesn't work (because the cached version
        // of settings overwrites everything else).
        final Map contextParams = requestContext.getRequestParameters(); 
        final Map cachedParams = this.cachedResult.context;
        final HashMap cachedKeys = new HashMap(cachedParams);
        cachedKeys.keySet().removeAll(contextParams.keySet());
        contextParams.putAll(cachedKeys);

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
    private void buildLuceneIndex(final ArrayOutputComponent.Result result) throws ProcessingException {
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