

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.adapters.localfilter;


import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.put.util.net.http.FormActionInfo;
import org.put.util.net.http.FormParameters;
import org.put.util.net.http.HTTPFormSubmitter;
import org.put.util.net.http.Parameter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Invokes a Filter component with a list of ordered documents and returns a sequence of
 * label:bitmap entries describing the discovered clusters.
 */
public class RemoteToLocalAdapter
    implements ClusteringFilterAdapter
{
    private URL serviceUrl;
    private Map parameters = new HashMap();

    /**
     * Constructs a remote to local adapter for a given service URL.
     */
    public RemoteToLocalAdapter(URL serviceUrl)
    {
        this.serviceUrl = serviceUrl;
    }

    /**
     * Sets a named parameter sent to the filter component as part of the query.
     */
    public void setParameter(String name, String value)
    {
        parameters.put(name, value);
    }


    /**
     * Clusters a sequence of <code>Hit</code> objects and returns an iterator of
     * <code>Cluster</code> objects at the top level of the hierarchy of clusters.
     *
     * @param hits An iterator over the hit list.
     * @param query Query provides hints for the clustering filter. It may be null, but if it
     *        exists, it will increase the quality of clusters.
     *
     * @throws RuntimeException In case of problems.
     */
    public Iterator clusterHits(Iterator hits, String query)
    {
        StringBuffer requestXml = new StringBuffer();

        // assemble the XML.
        requestXml.append("<searchresult>");

        // append <query tag?
        int id = 0;
        ArrayList hitsArray = new ArrayList();

        while (hits.hasNext())
        {
            Hit hit = (Hit) hits.next();

            hitsArray.add(hit);
            requestXml.append("<document id=\"" + (id++) + "\">");

            String title = hit.getTitle();

            if (title != null)
            {
                requestXml.append("<title>");
                requestXml.append(org.put.util.xml.XMLHelper.escapeElementEntities(title));
                requestXml.append("</title>");
            }

            String url = hit.getURL();

            if (url == null)
            {
                throw new RuntimeException("URL cannot be null for a hit.");
            }

            requestXml.append("<url>");
            requestXml.append(org.put.util.xml.XMLHelper.escapeElementEntities(url));
            requestXml.append("</url>");

            String snippet = hit.getSnippet();

            if (snippet != null)
            {
                requestXml.append("<snippet>");
                requestXml.append(org.put.util.xml.XMLHelper.escapeElementEntities(snippet));
                requestXml.append("</snippet>");
            }

            requestXml.append("</document>");
        }

        requestXml.append("</searchresult>");

        FormActionInfo actionInfo = new FormActionInfo(serviceUrl, "post");
        FormParameters queryArgs = new FormParameters();
        HTTPFormSubmitter submitter = new HTTPFormSubmitter(actionInfo);

        for (Iterator i = parameters.keySet().iterator(); i.hasNext();)
        {
            Object key = i.next();
            queryArgs.addParameter(new Parameter((String) key, parameters.get(key), false));
        }

        queryArgs.addParameter(new Parameter("carrot-xchange-data", requestXml.toString(), false));

        try
        {
            InputStream is = submitter.submit(queryArgs, null, "UTF-8");

            if (is == null)
            {
                HttpURLConnection connection = (java.net.HttpURLConnection) submitter.getConnection();

                try
                {
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                    {
                        throw new RuntimeException(
                            "Suspicious component response: (" + connection.getResponseCode()
                            + ") " + connection.getResponseMessage()
                        );
                    }
                }
                catch (java.io.FileNotFoundException e)
                {
                    // JDK BUG.
                    throw new RuntimeException(
                        "Syspicious component response: (JDK bug prevents analysis of HTTP header): "
                        + connection.getHeaderField(0) + ": " + e
                    );
                }
                catch (ClassCastException e)
                {
                    throw new RuntimeException("No output from component. Reason unknown: " + e);
                }
                catch (IOException e)
                {
                    throw new RuntimeException("No output from component. Reason unknown: " + e);
                }
            }

            // parse the result and construct a list of clusters.
            SAXBuilder builder = new SAXBuilder();
            Element root = builder.build(new InputStreamReader(is, "UTF-8")).getRootElement();

            List groups = root.getChildren("group");
            ArrayList clusters = new ArrayList(groups.size());

            for (Iterator i = groups.iterator(); i.hasNext();)
            {
                Element group = (Element) i.next();
                clusters.add(new JDOMCluster(group, hitsArray));
            }

            return clusters.iterator();
        }
        catch (JDOMException e)
        {
            throw new RuntimeException(
                "Unparsable response from filter component: " + e.toString()
            );
        }
        catch (IOException e)
        {
            throw new RuntimeException("Cannot process hits: " + e.toString());
        }
    }

    private static class JDOMCluster
        implements Cluster
    {
        private static final String [] NO_TITLE = new String[0];
        private final Element node;
        private final ArrayList hits;

        public JDOMCluster(Element node, ArrayList hits)
        {
            this.node = node;
            this.hits = hits;
        }

        public String [] getNamePhrases()
        {
            Element title = node.getChild("title");

            if (title == null)
            {
                return NO_TITLE;
            }

            List phrases = title.getChildren("phrase");

            String [] titlePhrases = new String[phrases.size()];
            int j = 0;

            for (Iterator i = phrases.iterator(); i.hasNext(); j++)
            {
                titlePhrases[j] = ((Element) i.next()).getTextTrim();
            }

            return titlePhrases;
        }


        /**
         * @see com.dawidweiss.carrot.adapters.localfilter.Cluster#getSubClusters()
         */
        public Iterator getSubClusters()
        {
            List groups = node.getChildren("group");

            if (groups.size() == 0)
            {
                return null;
            }

            List clusters = new LinkedList();

            for (Iterator i = groups.iterator(); i.hasNext();)
            {
                Element group = (Element) i.next();
                clusters.add(new JDOMCluster(group, hits));
            }

            return clusters.iterator();
        }


        /**
         * @see com.dawidweiss.carrot.adapters.localfilter.Cluster#getHits()
         */
        public Iterator getHits()
        {
            List groups = node.getChildren("document");

            if (groups.size() == 0)
            {
                return null;
            }

            List docs = new LinkedList();

            for (Iterator i = groups.iterator(); i.hasNext();)
            {
                Element doc = (Element) i.next();

                String value = doc.getAttributeValue("refid");

                if (value == null)
                {
                    throw new RuntimeException("Missing 'refid' attribute in the clustered XML.");
                }

                try
                {
                    int hitNumber = Integer.parseInt(value);
                    docs.add(hits.get(hitNumber));
                }
                catch (NumberFormatException e)
                {
                    throw new RuntimeException("Unexpected document identifier: " + value);
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    throw new RuntimeException(
                        "Referenced document index is out of the hits range. Weird. (" + value
                    );
                }
            }

            return docs.iterator();
        }
    }
}
