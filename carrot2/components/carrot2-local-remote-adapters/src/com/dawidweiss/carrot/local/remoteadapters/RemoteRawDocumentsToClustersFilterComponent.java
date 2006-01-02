
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.local.remoteadapters;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalFilterComponent;
import com.dawidweiss.carrot.core.local.LocalFilterComponentBase;
import com.dawidweiss.carrot.core.local.LocalInputComponent;
import com.dawidweiss.carrot.core.local.ProcessingException;
import com.dawidweiss.carrot.core.local.RequestContext;
import com.dawidweiss.carrot.core.local.clustering.RawClustersConsumer;
import com.dawidweiss.carrot.core.local.clustering.RawClustersProducer;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsProducer;
import com.dawidweiss.carrot.util.common.XMLSerializerHelper;
import com.dawidweiss.carrot.util.net.http.FormActionInfo;
import com.dawidweiss.carrot.util.net.http.FormParameters;
import com.dawidweiss.carrot.util.net.http.HTTPFormSubmitter;
import com.dawidweiss.carrot.util.net.http.Parameter;


public class RemoteRawDocumentsToClustersFilterComponent
    extends LocalFilterComponentBase
    implements LocalFilterComponent, RawDocumentsConsumer, 
               RawClustersProducer, RawDocumentsProducer
{
    private static final Set CAPABILITIES_COMPONENT =
        new HashSet(Arrays.asList(
            new Object [] {
                RawDocumentsConsumer.class,
                RawClustersProducer.class,
                RawDocumentsProducer.class
            }
        ));

    private static final Set CAPABILITIES_PREDECESSOR =
        new HashSet(Arrays.asList(
            new Object [] {
                RawDocumentsProducer.class
            }
        ));    

    private static final Set CAPABILITIES_SUCCESSOR =
        new HashSet(Arrays.asList(
            new Object [] {
                RawClustersConsumer.class
                /* we don't require successors to be
                 * also RawDocumentConsumers, but they can be.
                 */
            }
        ));    

    private final static Logger log = Logger.getLogger( RemoteRawDocumentsToClustersFilterComponent.class ); 

    private RawDocumentsConsumer rawDocumentConsumer;
    private RawClustersConsumer clustersConsumer;
    
    private URL remoteComponentUrl;
    
    private StringBuffer dataBuffer = new StringBuffer();

    private XMLSerializerHelper xmlSerializer = XMLSerializerHelper.getInstance();

    public RemoteRawDocumentsToClustersFilterComponent(URL remoteUrl)
    {
        this.remoteComponentUrl = remoteUrl;
    }

    public void setNext(LocalComponent next)
    {
        super.setNext(next);

        if (!(next instanceof RawClustersConsumer)) {
            log.warn("Successor component not an instance of: "
                + RawClustersConsumer.class.getName());
        } else {
            this.clustersConsumer = (RawClustersConsumer) next;
        }
        if (!(next instanceof RawDocumentsConsumer)) {
            log.warn("Successor component not an instance of: "
                + RawDocumentsConsumer.class.getName());
        } else {
            this.rawDocumentConsumer = (RawDocumentsConsumer) next;
        }
    }

    public void startProcessing(RequestContext requestContext) 
        throws ProcessingException
    {
        super.startProcessing(requestContext);

        // now wait until the predecessor provides Document references
        // via addDocument() method
        // and stream them to the filter component.
        dataBuffer.setLength(0);        
        dataBuffer.append("<searchresult>");
        
        Object query = 
            requestContext.getRequestParameters().get(LocalInputComponent.PARAM_QUERY);
        if (query != null) {
            dataBuffer.append("<query>");
            dataBuffer.append(xmlSerializer.toValidXmlText((String) query, true));
            dataBuffer.append("</query>");
        }
    }


    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#endProcessing()
     */
    public void endProcessing() throws ProcessingException
    {
        // finish the stream, acquire results from the remote component.
        dataBuffer.append("</searchresult>");

        System.out.println(dataBuffer.toString());

        try {
            List groups = queryFilterComponent();
            for (Iterator i = groups.iterator(); i.hasNext();)
            {
                Element group = (Element) i.next();
                
                if (this.clustersConsumer!=null) {
                    clustersConsumer.addCluster(
                        new ClusterElementWrapper(group));
                }
            }

        }
        catch (Exception e)
        {
            throw new ProcessingException(e);
        } finally {
            super.endProcessing();
        }
    }

    protected final List queryFilterComponent() throws UnsupportedEncodingException, IOException {
        FormActionInfo actionInfo = new FormActionInfo(this.remoteComponentUrl, "post");
        FormParameters queryArgs = new FormParameters();
        HTTPFormSubmitter submitter = new HTTPFormSubmitter(actionInfo);

        /*
        for (Iterator i = parameters.keySet().iterator(); i.hasNext();)
        {
            Object key = i.next();
            queryArgs.addParameter(new Parameter((String) key, 
                parameters.get(key), false));
        }
        */

        queryArgs.addParameter(new Parameter("carrot-xchange-data", 
            this.dataBuffer.toString(), false));

        InputStream is = null;
        try
        {
            is = submitter.submit(queryArgs, null, "UTF-8");

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
            SAXReader builder = new SAXReader();
            Element root;
            try {
                root = builder.read(new InputStreamReader(is, "UTF-8")).getRootElement();
            } catch (DocumentException e) {
                throw new IOException("Cannot parse response: " + e.toString());
            }

            List groups = root.elements("group");
            return groups;
        } finally {
            if (is!=null)
                is.close();
        }
    }


    /**
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer#addDocument(com.dawidweiss.carrot.core.local.clustering.RawDocument)
     */
    public void addDocument(RawDocument doc) throws ProcessingException
    {
        // append the contents of this document
        
        // we silently assume id's are xml-serializable 
        dataBuffer.append("\n<document id=\"" + doc.getId() + "\">");

        String title = doc.getTitle();

        if (title != null)
        {
            dataBuffer.append("<title>");
            dataBuffer.append(xmlSerializer.toValidXmlText(title, false));
            dataBuffer.append("</title>");
        }

        String url = doc.getUrl();
        if (url != null) {
            dataBuffer.append("<url>");
            dataBuffer.append(xmlSerializer.toValidXmlText(url, false));
            dataBuffer.append("</url>");
        }

        String snippet = (String) doc.getProperty(RawDocument.PROPERTY_SNIPPET);
        if (snippet != null)
        {
            dataBuffer.append("<snippet>");
            dataBuffer.append(xmlSerializer.toValidXmlText(snippet, false));
            dataBuffer.append("</snippet>");
        }
        dataBuffer.append("</document>");
        
        if (rawDocumentConsumer != null) {
            rawDocumentConsumer.addDocument(doc);
        }
    }


    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        
        // clean up.
        this.rawDocumentConsumer = null;
        this.clustersConsumer = null;
        this.remoteComponentUrl = null;
        this.rawDocumentConsumer = null;
        dataBuffer.setLength(0);
    }

    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

}
