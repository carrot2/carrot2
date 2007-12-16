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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Passes down the processing chain query results read from an XML stream in the Carrot<sup>2</sup> format. This
 * component expects any of the following request parameters:
 * <ul>
 * <li>{@link #XML_STREAM} initialized to an {@link java.io.InputStream} with XML data.</li>
 * </ul>
 * 
 * @author Dawid Weiss
 */
public class XmlStreamInputComponent extends LocalInputComponentBase
{
    /**
     * An XML stream to read from.
     */
    public static final String XML_STREAM = "input:xml-stream";

    /** Capabilities required from the next component in the chain. */
    private final static Set SUCCESSOR_CAPABILITIES = toSet(RawDocumentsConsumer.class);

    /** This component's capabilities. */
    private final static Set COMPONENT_CAPABILITIES = toSet(RawDocumentsProducer.class, RawClustersProducer.class);

    /**
     * Represents a query result containing a list of {@link RawDocument}s, {@link RawCluster}s and the query that
     * returned these documents.
     */
    public final static class QueryResult
    {
        /** The query */
        public String query;

        /** A list of {@link RawDocument}s parsed from the stream. */
        public List rawDocuments;

        /** A list of {@link RawCluster}s parsed from the stream. */
        public List rawClusters;
    }

    /** The {@link RawDocumentsConsumer} to feed. */
    private RawDocumentsConsumer rawDocumentConsumer;

    /** The {@link RawClustersConsumer} to feed. */
    private RawClustersConsumer rawClustersConsumer;

    /**
     * Query issued to this component.
     */
    private String originalQuery;

    /*
     */
    public Set getComponentCapabilities()
    {
        return COMPONENT_CAPABILITIES;
    }

    /*
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return SUCCESSOR_CAPABILITIES;
    }

    /*
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);

        rawDocumentConsumer = null;
        if (next instanceof RawDocumentsConsumer)
        {
            rawDocumentConsumer = (RawDocumentsConsumer) next;
        }

        rawClustersConsumer = null;
        if (next instanceof RawClustersConsumer)
        {
            rawClustersConsumer = (RawClustersConsumer) next;
        }
    }

    /*
     * 
     */
    public void setQuery(String query)
    {
        this.originalQuery = query;
    }

    /**
     * Return the current query for subclasses.
     */
    protected String getQuery()
    {
        return this.originalQuery;
    }

    /*
     * 
     */
    public final void startProcessing(RequestContext requestContext) throws ProcessingException
    {
        super.startProcessing(requestContext);

        final InputStream is = getInputXML(requestContext);
        try
        {
            final int requestedResults = getRequestedResultsCount(requestContext);

            // Load query results from the file
            final SAXReader reader = new SAXReader();
            final Element root = reader.read(is).getRootElement();

            // Pass the actual document count
            passQueryResult(root, requestContext, requestedResults);

            // Pass additional information
            passAdditionalInformation(root, requestContext);
        }
        catch (Exception e)
        {
            throw new ProcessingException("Problems parsing XML stream.", e);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                // ignore
            }
        }
    }

    /**
     * Returns the requested results count.
     * 
     * @param requestContext
     * @return the requested results count.
     */
    private int getRequestedResultsCount(RequestContext requestContext)
    {
        int requestedResults;
        if (requestContext.getRequestParameters().containsKey(LocalInputComponent.PARAM_REQUESTED_RESULTS))
        {
            requestedResults = Integer.parseInt(requestContext.getRequestParameters().get(
                LocalInputComponent.PARAM_REQUESTED_RESULTS).toString());
        }
        else
        {
            requestedResults = -1;
        }
        return requestedResults;
    }

    /**
     * Invoked at the end of the {@link #startProcessing(RequestContext)} method to allow subclasses to pass some
     * additional information to the next component in the chain. Implementation of this method in this class is empty.
     * 
     * @param root
     * @param requestContext
     */
    protected void passAdditionalInformation(Element root, RequestContext requestContext)
    {
    }

    /**
     * Callback method invoked from {@link #startProcessing(RequestContext)} and responsible for locating the input XML
     * stream.
     */
    protected InputStream getInputXML(RequestContext requestContext) throws ProcessingException
    {
        final InputStream is = (InputStream) requestContext.getRequestParameters().get(XML_STREAM);

        if (is == null)
        {
            throw new ProcessingException("This component expects " + XML_STREAM + " parameter in the request context.");
        }

        return is;
    }

    /**
     * Passes the query result to the next component in the chain.
     * 
     * @param root root element of the input XML
     * @param requestContext
     * @param requestedResults the number of requested results
     * @throws ProcessingException if an error occurs
     */
    protected void passQueryResult(Element root, RequestContext requestContext, int requestedResults)
        throws ProcessingException
    {
        // Extract documents
        final QueryResult queryResult = extractQueryResult(root, requestedResults);

        requestContext.getRequestParameters().put(LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS,
            new Integer(queryResult.rawDocuments.size()));

        requestContext.getRequestParameters().put(LocalInputComponent.PARAM_QUERY, queryResult.query);

        for (Iterator iter = queryResult.rawDocuments.iterator(); iter.hasNext();)
        {
            final RawDocument rawDocument = (RawDocument) iter.next();
            rawDocumentConsumer.addDocument(rawDocument);
        }

        if (rawClustersConsumer != null)
        {
            for (Iterator iter = queryResult.rawClusters.iterator(); iter.hasNext();)
            {
                final RawCluster rawCluster = (RawCluster) iter.next();
                rawClustersConsumer.addCluster(rawCluster);
            }
        }
    }

    /**
     * Loads {@link QueryResult} from a stream in the Carrot<sup>2</sup> XML format.
     * 
     * @param inputStream stream to read from
     * @param requestedResults the number of results to load
     * @return loaded query result
     * @throws DocumentException if a parsing problem occurs
     */
    public static QueryResult loadQueryResult(InputStream inputStream, int requestedResults) throws DocumentException
    {
        final SAXReader reader = new SAXReader();
        final Element root = reader.read(inputStream).getRootElement();
        return extractQueryResult(root, requestedResults);
    }

    /**
     * Parses an XML element in the Carrot<sup>2</sup> format into a {@link QueryResult}.
     * 
     * @param root XML element in the Carrot<sup>2</sup> format
     * @param requestedResults the number of results to parse
     * @return parsed query result
     */
    public static QueryResult extractQueryResult(Element root, int requestedResults)
    {
        final QueryResult queryResult = new QueryResult();

        final List documents = root.elements("document");
        int matchingDocuments = documents.size();
        if (requestedResults > 0 && requestedResults < matchingDocuments)
        {
            matchingDocuments = requestedResults;
        }

        // Save the query
        final Element queryElement = root.element("query");
        if (queryElement != null)
        {
            queryResult.query = queryElement.getText();
        }

        // Save documents
        final HashMap docsById = new HashMap();
        queryResult.rawDocuments = new ArrayList(matchingDocuments);
        int id = 0;
        for (Iterator i = documents.iterator(); i.hasNext() && id < matchingDocuments; id++)
        {
            final Element docElem = (Element) i.next();

            final String url;
            if (docElem.element("url") != null)
            {
                url = docElem.elementText("url");
            }
            else
            {
                url = "nourl://document-id-" + id;
            }

            final String title;
            if (docElem.element("title") != null)
            {
                title = docElem.elementText("title");
            }
            else
            {
                title = null;
            }

            final String snippet;
            if (docElem.element("snippet") != null)
            {
                snippet = docElem.elementText("snippet");
            }
            else
            {
                snippet = null;
            }

            final String idString;
            if (docElem.attributeValue("id") != null)
            {
                idString = docElem.attributeValue("id");
            }
            else
            {
                idString = Integer.toString(id);
            }

            final RawDocument rawDoc = new RawDocumentSnippet(idString, title, snippet, url, 0);
            if (null != docsById.put(idString, rawDoc)) {
                throw new RuntimeException("Error in the input XML, duplicated document identifier: " + idString);
            }
            
            addArrayProperties(docElem, rawDoc, "sources", RawDocument.PROPERTY_SOURCES);
            addArrayProperties(docElem, rawDoc, "keywords", RawDocument.PROPERTY_KEYWORDS);
            
            queryResult.rawDocuments.add(rawDoc);
        }

        // Save clusters
        queryResult.rawClusters = new ArrayList();
        final List clusters = root.elements("group");
        for (Iterator i = clusters.iterator(); i.hasNext();)
        {
            final Element clusterElem = (Element) i.next();
            final RawCluster cluster = parseSubcluster(docsById, clusterElem);
            queryResult.rawClusters.add(cluster);
        }

        return queryResult;
    }

    private static void addArrayProperties(final Element docElem,
        final RawDocument rawDoc, final String parentElementName,
        final String propertyName)
    {
        if (docElem.element(parentElementName) != null)
        {
            List sources = docElem.element(parentElementName).elements();
            String [] sourcesArray = new String [sources.size()];
            int j = 0;
            for (Iterator it = sources.iterator(); it.hasNext(); j++)
            {
                Element sourceElement = (Element) it.next();
                sourcesArray[j] = sourceElement.getText();
            }
            rawDoc.setProperty(propertyName, sourcesArray);
        }
    }

    /*
     * 
     */
    private static RawCluster parseSubcluster(HashMap docsById, Element clusterElem)
    {
        final RawClusterBase cluster = new RawClusterBase();

        final String scoreAttr = clusterElem.attributeValue("score");
        if (scoreAttr != null)
        {
            cluster.setScore(Double.parseDouble(scoreAttr));
        }

        final Element titleElem = clusterElem.element("title");
        if (titleElem != null)
        {
            for (Iterator iterator = titleElem.elements("phrase").iterator(); iterator.hasNext();)
            {
                final Element phraseElem = (Element) iterator.next();
                cluster.addLabel(phraseElem.getText());
            }
        }

        for (Iterator iterator = clusterElem.elements("document").iterator(); iterator.hasNext();)
        {
            final Element documentElem = (Element) iterator.next();
            final String refid = documentElem.attributeValue("refid");
            final RawDocument rawDoc = (RawDocument) docsById.get(refid);
            if (rawDoc == null)
            {
                // This means that either the input is malformed (cluster references non-existent document)
                // or the number of requested documents was lower than the one from which clusters were
                // originally generated. 
                continue;
            }
            cluster.addDocument(rawDoc);
        }

        for (Iterator iterator = clusterElem.elements("group").iterator(); iterator.hasNext();)
        {
            final Element subclusterElem = (Element) iterator.next();
            cluster.addSubcluster(parseSubcluster(docsById, subclusterElem));
        }

        return cluster;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        rawDocumentConsumer = null;
    }
}
