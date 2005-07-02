/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.input.localcache;

import java.io.File;
import java.util.*;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;

/**
 * Implements a local input component that reads data
 * from a cache files saved by a remote controller.
 * 
 * <p>This is usually only useful for debugging, but 
 * who knows.</p>
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class RemoteCacheAccessLocalInputComponent extends
    LocalInputComponentBase implements RawDocumentsProducer
{
	private static Logger log = Logger.getLogger(RemoteCacheAccessLocalInputComponent.class);
	
    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsConsumer.class }));

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsProducer.class }));

    /** Current "query". See the docs for query formats. */
    private String query;

    /** Current RawDocumentsConsumer to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /**
     * A cached queries store.
     */
	private CachedQueriesStore store;

    /** Current request context */
    private RequestContext requestContext;
    
    /**
     * Creates a new instance of the component with no lookup directories
     * for "raw" queries. Only absolute file pointers and directory
     * queries will be accepted.
     */
    public RemoteCacheAccessLocalInputComponent() {
    }

    /**
     * Creates a new instance of the component with a lookup directory for
     * 'raw' queries. If a query matches any of the loaded cached results,
     * those results will be returned. See the docs. for specific query
     * parameter issues.
     */
    public RemoteCacheAccessLocalInputComponent(CachedQueriesStore store) {
    	this.store = store;
    }


    /*
     * @see com.dawidweiss.carrot.core.local.LocalInputComponent#setQuery(java.lang.String)
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return COMPONENT_CAPABILITIES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return SUCCESSOR_CAPABILITIES;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        query = null;
        rawDocumentConsumer = null;
        requestContext = null;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalInputComponent#setNext(com.dawidweiss.carrot.core.local.LocalComponent)
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        if (next instanceof RawDocumentsConsumer)
        {
            rawDocumentConsumer = (RawDocumentsConsumer) next;
        }
        else
        {
            rawDocumentConsumer = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#startProcessing(com.dawidweiss.carrot.core.local.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        super.startProcessing(requestContext);

        // Store the current context
        this.requestContext = requestContext;
        
        // parse the query and see what it is.
        String query = this.query.trim();
        
        if (query.startsWith("file:")) {
        	// it is a direct file request.
        	handleDirectFileRequest(query.substring("file:".length()));
        } else if (query.startsWith("dump:")) {
        	handleStoreDump();
        } else {
        	// treat as if it were a raw query.
        	if (this.store == null)
        		throw new ProcessingException("Cannot serve cached queries if no cached files store has been set.");
        	handleRawQuery(query);
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getName()
     */
    public String getName()
    {
        return "Cache Input";
    }

    /**
	 * @param query2
	 */
	private void handleRawQuery(String query) throws ProcessingException {
    	if (store == null)
    		throw new ProcessingException("There is no store to query. Initialize the component with a queries' store.");

    	// split the query and check if there is a reference to a specific component.
    	String component = null;
    	
    	int index = query.indexOf("component:");
    	if (index >= 0) {
    		int last = index;
    		while (last < query.length() && query.charAt(last) != ' ') {
    			last++;
    		}
    		component = query.substring(index + "component:".length(), last);
    		query = query.substring(0, index) + (last < query.length() ? query.substring(last) : "");
    		query = query.trim();
    	}
    	
    	ZIPCachedQuery zcq = store.getQuery( query, component );
    	if (zcq == null) 
    		throw new ProcessingException("No cached query of this name/ component identifier combination.");
		
    	try {
			SAXReader reader = new SAXReader();
			Element root = reader.read( zcq.getData() ).getRootElement();

			pushAsLocalData( root );
		} catch (Exception e) {
			throw new ProcessingException("Problems opening cached query.",e);
		}
	}

	/**
	 * 
	 */
	private void handleStoreDump() throws ProcessingException {
    	// it is a request to dump the content of the store.
    	if (store == null)
    		throw new ProcessingException("There is no store to dump. Initialize the component with a store.");
    	
    	List cachedQueries = store.getCachedQueries();

    	int id = 0;
    	for (Iterator i = cachedQueries.iterator(); i.hasNext(); id++) {
    		ZIPCachedQuery zcq = (ZIPCachedQuery) i.next();

			RawDocument document = new RawDocumentSnippet(new Integer(id), 
					"Query store dump, query #" + (id + 1), 
					"query: " + zcq.getQuery() + ", component: "
					+ zcq.getComponentId() + ".", "about://blank", 0);
			this.rawDocumentConsumer.addDocument(document);
    	}
	}

	/**
	 * Handle a local file access request.
	 */
	private void handleDirectFileRequest(String file) throws ProcessingException {
		File fh = new File( file );
		if (!fh.isFile() || !fh.canRead()) {
			throw new ProcessingException("Cannot read file: " + fh.getAbsolutePath());
		}

		try {
			ZIPCachedQuery zcq = new ZIPCachedQuery(fh);
			SAXReader reader = new SAXReader();
			Element root = reader.read( zcq.getData() ).getRootElement();
			pushAsLocalData( root );
		} catch (Exception e) {
			throw new ProcessingException("Problems opening cached query.",e);
		}
		
	}

	private void pushAsLocalData(Element root) throws ProcessingException {
		List documents = root.elements("document");

        int matchingDocuments = documents.size();
        Map params = requestContext.getRequestParameters();
        if (params.containsKey(LocalInputComponent.PARAM_REQUESTED_RESULTS))
        {
            int requestedResults;
            requestedResults = Integer.parseInt(params.get(
                LocalInputComponent.PARAM_REQUESTED_RESULTS).toString());
            
            if (requestedResults < matchingDocuments)
            {
                matchingDocuments = requestedResults;
            }
        }
        
        // Pass the actual document count
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS,
            new Integer(matchingDocuments));

        // Pass the query
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_QUERY, root.element("query").getText());

		int id = 0;
		for (Iterator i = documents.iterator(); i.hasNext() && id < matchingDocuments; id++) {
			Element docElem = (Element) i.next();

			String url = docElem.elementText("url");
			String title = docElem.elementText("title");
			String snippet = docElem.elementText("snippet");

			RawDocument document = new RawDocumentSnippet(new Integer(id), title, snippet, url, 0);
			this.rawDocumentConsumer.addDocument(document);
		}
	}
}
