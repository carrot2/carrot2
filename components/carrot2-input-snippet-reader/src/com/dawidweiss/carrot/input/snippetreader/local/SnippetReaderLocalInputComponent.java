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
package com.dawidweiss.carrot.input.snippetreader.local;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalControllerContext;
import com.dawidweiss.carrot.core.local.LocalInputComponent;
import com.dawidweiss.carrot.core.local.LocalInputComponentBase;
import com.dawidweiss.carrot.core.local.ProcessingException;
import com.dawidweiss.carrot.core.local.RequestContext;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentBase;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsProducer;
import com.dawidweiss.carrot.input.snippetreader.readers.WebSnippetReader;

/**
 * Implements a local input component that reads data
 * from a remote location using regular expressions (snippet reader).
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class SnippetReaderLocalInputComponent extends
    LocalInputComponentBase implements RawDocumentsProducer
{
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
     * Snippet reader (configured component that reads snippets from an external
     * source).
     */
    private WebSnippetReader snippetReader;
    
    /**
     * Creates a new instance of the component with no "source" attached
     * yet. Preconfigure the component using {@link #setConfigurationXml(InputStream is)}.
     */
    public SnippetReaderLocalInputComponent() {
    }

    /**
     * Preconfigures the component to use the given configuration.
     * 
     * @throws Exception If an exception occurrs.
     */
    public void setConfigurationXml(InputStream is) throws Exception {
        if (this.snippetReader != null)
            throw new IllegalStateException("This component can be initialized once, or via query parameters at runtime.");

        // add an instance of this class as default handler
        SAXReader builder = new SAXReader();
        builder.setValidation(false);

        final Document config = builder.read(is);
        snippetReader = new WebSnippetReader(config.getRootElement());
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public Set getComponentCapabilities()
    {
        return COMPONENT_CAPABILITIES;
    }

    public Set getRequiredSuccessorCapabilities()
    {
        return SUCCESSOR_CAPABILITIES;
    }

    public void flushResources()
    {
        super.flushResources();
        query = null;
        rawDocumentConsumer = null;
    }

    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        rawDocumentConsumer = (RawDocumentsConsumer) next;
    }

    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        requestContext.getRequestParameters().put(LocalInputComponent.PARAM_QUERY, this.query);
        super.startProcessing(requestContext);

        // Number of requested results and the "start at" parameter.
        int resultsRequested = super.getIntFromRequestContext(requestContext,
                LocalInputComponent.PARAM_REQUESTED_RESULTS, 100);

        try {
            final Vector v = snippetReader.getSnippets(query, resultsRequested);

            // the result is a sequence of three strings per
            // result. wrap it and pass it to the successor.
            for (int i = 0; i < v.size(); i += 3) {
                final Integer id = new Integer(i);
                final int offset = i;
                
                RawDocument rdoc = new RawDocumentBase(
                        (String) v.get(offset + 1),
                        (String) v.get(offset),
                        (String) v.get(offset+2)) {
                    public Object getId() {
                        return id;
                    }
                };

                this.rawDocumentConsumer.addDocument(rdoc);
            }
        } catch (ProcessingException e) {
            throw e;
        } catch (Exception e) {
            throw new ProcessingException("Could not process query.", e);
        }
    }
    

    public void init(LocalControllerContext context)
            throws InstantiationException {
        super.init(context);

        // initializes the component for use.
        if (this.snippetReader == null) {
            throw new InstantiationException("Configuration must be provided" +
            		" for this snippet reader.");
        }
    }

    public String getName()
    {
        return "Snippet Reader Input";
    }
}
