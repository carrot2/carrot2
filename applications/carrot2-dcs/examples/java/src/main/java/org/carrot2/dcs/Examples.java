
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

package org.carrot2.dcs;

import java.io.*;
import java.net.*;
import java.util.*;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

/**
 * Contains several examples of clustering using the Document Clustering Server.
 */
final class Examples
{
    /** Shared path to the XML file to be clustered. */
    private static final String XML_FILE_PATH = "../shared/data-mining.xml";

    /** Shared XML feed. */
    private static final String XML_FEED = 
        "http://search.carrot2.org/stable/xml?source=web&type=CARROT2&q=test&results=20";

    /** Shared DCS address. */
    private static final URI dcsURI;
    static
    {
        try
        {
            dcsURI = new URI("http://localhost:8080/dcs/rest");
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Anything capable of formatting, sending and parsing a multipart HTTP POST.
     */
    private final IHttpMultipartPostProvider httpPoster;

    /*
     * 
     */
    public Examples(IHttpMultipartPostProvider httpPoster)
    {
        this.httpPoster = httpPoster;
    }

    /**
     * Cluster data from an XML file (local).
     */
    public void clusterFromFile() throws IOException
    {
        final Map<String, String> attributes = new LinkedHashMap<String, String>();

        System.out.println("## Clustering documents from a local file");

        /*
         * Note the optional query attribute, we can provide it to avoid creation of
         * trivial clusters.
         */

        attributes.put("dcs.c2stream", new String(
            StreamUtils.readFullyAndClose(
                new FileInputStream(XML_FILE_PATH)), "UTF-8"));
        attributes.put("query", "data mining");

        displayResults(httpPoster.post(dcsURI, attributes));
    }

    /**
     * Cluster data from an external XML stream feed (providing an URL to that feed).
     */
    public void clusterFromRemoteXML() throws IOException
    {
        final Map<String, String> attributes = new LinkedHashMap<String, String>();

        System.out.println("## Clustering documents from a remote XML feed");
        attributes.put("dcs.source", "xml");
        attributes.put("dcs.algorithm", "stc");
        attributes.put("XmlDocumentSource.xml", XML_FEED);

        displayResults(httpPoster.post(dcsURI, attributes));
    }

    /**
     * Cluster data retrieved from a search engine or some other source registered in the
     * DCS as a document source.
     */
    public void clusterFromSearchEngine() throws IOException
    {
        final Map<String, String> attributes = new LinkedHashMap<String, String>();

        /*
         * For this request, we will pass some additional attributes to the default
         * algorithm and ask to skip the fetched documents in the output (retrieve
         * clusters only).
         */

        System.out.println("## Clustering search results from a search engine");

        // We use etools meta search engine input component.
        attributes.put("dcs.source", "etools");
        attributes.put("query", "test");
        attributes.put("results", "20");
        attributes.put("dcs.algorithm", "lingo");
        attributes.put("dcs.clusters.only", "true");

        // Some customized algorithm parameters.
        attributes.put("LingoClusteringAlgorithm.desiredClusterCountBase", "10");
        attributes.put("LingoClusteringAlgorithm.factorizationQuality", "LOW");
        attributes.put("LingoClusteringAlgorithm.factorizationFactory",
            "org.carrot2.matrix.factorization.PartialSingularValueDecompositionFactory");

        displayResults(httpPoster.post(dcsURI, attributes));
    }

    /**
     * Runs all examples.
     */
    public void runAllExamples() throws IOException
    {
        clusterFromFile();
        clusterFromRemoteXML();
        clusterFromSearchEngine();
    }

    /**
     * Run all examples with all HTTP POST providers.  
     */
    public static void main(String [] args) throws IOException
    {
        IHttpMultipartPostProvider [] providers = {
            new HttpClientPostProvider(),
            new JaxRsPostProvider()
        };

        for (IHttpMultipartPostProvider provider : providers)
        {
            new Examples(provider).runAllExamples();
        }
    }
    
    /**
     * Simple parsing and display of the response. This method uses dom4j for parsing XML,
     * feel free to use anything that comes handy.
     */
    @SuppressWarnings("unchecked")
    private static void displayResults(InputStream results) throws IOException
    {
        try
        {
            final SAXReader reader = new SAXReader();
            final Document document = reader.read(results);
            final Iterator<Element> i = document.getRootElement().elementIterator("group");
            while (i.hasNext())
            {
                final Element group = i.next();
                display(group, 1);
            }
            System.out.println();
        }
        catch (DocumentException e)
        {
            throw new IOException("Could not parse response: " + e.getMessage());
        }
        finally
        {
            if (results != null)
            {
                results.close();
            }
        }
    }

    /**
     * Display a single cluster and its sub-clusters.
     */
    @SuppressWarnings({"unchecked"})
    private static void display(Element group, int level)
    {
        final int recursiveDocumentCount = Integer.parseInt(group.attribute("size").getValue());
        final String label = group.element("title").elementText("phrase");

        for (int i = 0; i < level; i++) System.out.print("  ");
        System.out.println(label + " [" + recursiveDocumentCount + " document(s)]");
        final Iterator<Element> i = group.elementIterator("group");
        while (i.hasNext())
        {
            display(i.next(), level + 1);
        }
    }
}
