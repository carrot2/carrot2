
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.xml;

import java.util.List;
import java.util.Map;

import javax.xml.transform.Templates;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.source.SimpleSearchEngine;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.resource.IResource;

/**
 * A base class for implementing data sources based on XML/XSLT. The XSLT stylesheet will
 * be loaded once during component initialization and cached for all further requests.
 */
@Bindable
public abstract class RemoteXmlSimpleSearchEngineBase extends SimpleSearchEngine
{
    /** A helper class that groups common functionality for XML/XSLT based data sources. */
    private final XmlDocumentSourceHelper xmlDocumentSourceHelper = new XmlDocumentSourceHelper();

    /** XSLT transformation to Carrot2 DTD */
    private Templates toCarrot2Xslt;

    @Override
    public void init(IControllerContext context)
    {
        super.init(context);

        toCarrot2Xslt = xmlDocumentSourceHelper.loadXslt(getXsltResource());
    }

    @Override
    public void beforeProcessing() throws ProcessingException
    {
        super.beforeProcessing();
        if (toCarrot2Xslt == null)
        {
            throw new ProcessingException("XSLT stylesheet must not be null");
        }
    }

    @Override
    protected SearchEngineResponse fetchSearchResponse() throws Exception
    {
        final String serviceURL = buildServiceUrl();
        final SearchEngineResponse response = new SearchEngineResponse();

        final ProcessingResult processingResult = xmlDocumentSourceHelper
            .loadProcessingResult(serviceURL, toCarrot2Xslt, getXsltParameters(),
                response.metadata, getUser(), getPassword());

        final List<Document> documents = processingResult.getDocuments();
        if (documents != null)
        {
            response.results.addAll(documents);
            final Map<String, Object> resultAttributes = processingResult.getAttributes();
            response.metadata
                .put(SearchEngineResponse.RESULTS_TOTAL_KEY, resultAttributes
                    .containsKey(AttributeNames.RESULTS_TOTAL) ? resultAttributes
                    .get(AttributeNames.RESULTS_TOTAL) : (long) documents.size());
        }
        else
        {
            response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY, 0L);
        }

        afterFetch(response);

        return response;
    }

    /**
     * Returns the XSLT stylesheet that transforms the custom XML into Carrot2 compliant
     * XML. This method will be called once during component initialization.
     * Initialization time attributes will have been bound before the call to this method.
     */
    protected abstract IResource getXsltResource();

    /**
     * Returns parameters to be passed to the XSLT transformer. This method will be called
     * once per processing cycle. Processing-time attributes will have been bound before
     * this method the call to this method. The default implementation returns
     * <code>null</code>.
     */
    protected Map<String, String> getXsltParameters()
    {
        return null;
    }

    /**
     * Builds the URL from which XML stream will be fetched. This method will be called
     * once per request processing cycle. Processing-time attributes will have been bound
     * before this method the call to this method.
     */
    protected abstract String buildServiceUrl();

    /**
     * Returns the user name to use for HTTP Basic Authentication.
     */
    protected String getUser()
    {
        return null;
    }

    /**
     * Returns the password to use for HTTP Basic Authentication.
     */
    protected String getPassword()
    {
        return null;
    }
}
