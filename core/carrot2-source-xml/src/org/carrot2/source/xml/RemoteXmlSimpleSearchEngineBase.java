
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.xml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Templates;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.carrot2.core.Document;
import org.carrot2.core.HttpAuthHub;
import org.carrot2.core.IControllerContext;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.source.SimpleSearchEngine;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.httpclient.HttpRedirectStrategy;
import org.carrot2.util.httpclient.HttpUtils;
import org.carrot2.util.resource.IResource;

/**
 * A base class for implementing data sources based on XML/XSLT. The XSLT stylesheet will
 * be loaded once during component initialization and cached for all further requests.
 */
@Bindable
public abstract class RemoteXmlSimpleSearchEngineBase extends SimpleSearchEngine
{
    /** A helper class that groups common functionality for XML/XSLT based data sources. */
    public final XmlDocumentSourceHelper xmlDocumentSourceHelper = new XmlDocumentSourceHelper();

    /**
     * HTTP redirect response strategy (follow or throw an error).
     */
    @Input
    @Processing
    @Attribute
    @Label("HTTP redirect strategy")
    @Level(AttributeLevel.MEDIUM)
    @Group(SimpleSearchEngine.SERVICE)
    @Internal
    public HttpRedirectStrategy redirectStrategy = HttpRedirectStrategy.NO_REDIRECTS; 

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

        final ProcessingResult processingResult = loadProcessingResult(
                serviceURL, 
                toCarrot2Xslt, 
                getXsltParameters(),
                response.metadata, 
                getUser(), 
                getPassword(),
                redirectStrategy);

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
        afterFetch(response, processingResult);

        return response;
    }


    /**
     * Invoked after the response has been partially parsed and {@link ProcessingResult}
     * deserialized. 
     */
    protected void afterFetch(SearchEngineResponse response, ProcessingResult processingResult)
    {
        // Empty by default.
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
        return HttpAuthHub.getUser();
    }

    /**
     * Returns the password to use for HTTP Basic Authentication.
     */
    protected String getPassword()
    {
        char[] password = HttpAuthHub.getPassword();
        return password == null ? null : new String(password);
    }

    /**
     * Loads a {@link ProcessingResult} from the provided remote URL, applying XSLT
     * transform if specified. This method can handle gzip-compressed streams if supported
     * by the data source.
     * 
     * @param metadata if a non-<code>null</code> map is provided, request metadata will
     *            be put into the map.
     * @param user if not <code>null</code>, the user name to use for HTTP Basic
     *            Authentication
     * @param password if not <code>null</code>, the password to use for HTTP Basic
     *            Authentication
     */
    protected ProcessingResult loadProcessingResult(String url, Templates stylesheet,
        Map<String, String> xsltParameters, Map<String, Object> metadata, String user,
        String password, HttpRedirectStrategy redirectStrategy) throws Exception
    {
        final HttpUtils.Response response = HttpUtils.doGET(
            url, 
            null, null, 
            user, password, 
            xmlDocumentSourceHelper.timeout * 1000,
            redirectStrategy.value());
    
        final InputStream carrot2XmlStream = response.getPayloadAsStream();
        final int statusCode = response.status;
    
        if (statusCode == HttpStatus.SC_OK)
        {
            metadata.put(SearchEngineResponse.COMPRESSION_KEY, response.compression);
            return xmlDocumentSourceHelper.loadProcessingResult(carrot2XmlStream, stylesheet, xsltParameters);
        }
        else
        {
            throw new HttpResponseException(statusCode, response.statusMessage);
        }
    }
}
