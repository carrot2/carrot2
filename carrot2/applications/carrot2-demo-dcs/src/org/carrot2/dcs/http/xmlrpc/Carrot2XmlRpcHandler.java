
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs.http.xmlrpc;

import java.util.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.carrot2.core.MissingProcessException;
import org.carrot2.core.clustering.*;
import org.carrot2.dcs.*;
import org.carrot2.util.PerformanceLogger;
import org.carrot2.util.StringUtils;

/**
 * A simple XML-RPC handler for clustering requests.
 * 
 * @author Dawid Weiss
 */
public class Carrot2XmlRpcHandler
{
    /** Custom logger. */
    private final static Logger logger = Logger.getLogger(Carrot2XmlRpcHandler.class);

    /** DCS logger. */
    private final Logger dcsLogger;

    /** Default application configuration. */
    private AppConfig config;

    /**
     * Creates a new XML-RPC handler with the given configuration.
     */
    public Carrot2XmlRpcHandler(AppConfig config)
    {
        this.config = config;
        this.dcsLogger = config.getConsoleLogger();
    }

    /**
     * <p>
     * Clusters documents in <code>documents</code> array and returns an array of
     * clusters to the caller.
     * 
     * <p>
     * The input format is a sequence (vector) of tuples describing documents. Here is an
     * example:
     * 
     * <pre>
     * String id           // Document #1
     * String url
     * String title
     * String snippet
     * String id           // Document #1
     * String url
     * String title
     * String snippet
     * ...
     * String id           // Document #n
     * String url
     * String title
     * String snippet
     * </pre>
     * 
     * Note that if an element is empty, <code>null</code> should be passed (all
     * elements must be present).
     * <p>
     * The output format is a sequence (vector) of maps, each describing a
     * single group. Here is an example:
     * <pre>
     * String label
     * vector&lt;String&gt; documents
     * vector&lt;Map&gt; subclusters
     * </pre>
     * Where subclusters are defined recursively in an identical way.
     * 
     * @param query The query.
     * @param processingOptions Configuration options. See {@link ProcessingOptionNames}
     * for possible keys.
     * @param processParameters Parameters passed to the clustering process.
     * @param documents Input vector of documents.
     */
    public Vector doCluster(String query, Hashtable processingOptions, Hashtable processParameters, 
        Vector documents)
    {
        final PerformanceLogger plogger = new PerformanceLogger(Level.DEBUG, dcsLogger);
        plogger.start("Processing XMLRPC");
        try {
            plogger.start("Converting input");
            // Documents are ordered in the input argument: every document has these fields
            // (exactly): id, url, title, snippet. Wrap the documents into a 
            // {@link RawDocumentBase} interface and pass them for clustering.
            final ArrayList wrappedDocuments = new ArrayList(documents.size() / 4);
            final int max = documents.size();
            for (int i = 0; i < max; i = i + 4)
            {
                final String id = (String) documents.get(i);
                final String url = (String) documents.get(i + 1);
                final String title = (String) documents.get(i + 2);
                final String snippet = (String) documents.get(i + 3);
                wrappedDocuments.add(new RawDocumentBase(url, title, snippet)
                {
                    public Object getId()
                    {
                        return id;
                    }
                });
            }
            plogger.end();

            final Vector result;
            try
            {
                final HashMap overrides = new HashMap(config.getProcessingDefaults());
                if (processingOptions != null)
                {
                    overrides.putAll(processingOptions);
                }

                final HashMap requestParams = new HashMap();
                if (processParameters != null)
                {
                    requestParams.putAll(processParameters);
                }

                final List outputClusters = ProcessingUtils.clusterRawDocuments(plogger,
                    config.getControllerContext().getController(), overrides, query,
                    wrappedDocuments, requestParams).clusters;

                plogger.start("Converting output");
                final StringBuffer tmpbuf = new StringBuffer();
                result = new Vector();
                addSubclusters(outputClusters, result, tmpbuf);
                plogger.end();
            }
            catch (MissingProcessException e)
            {
                final String message = "Unavailable clustering process: "
                    + StringUtils.chainExceptionMessages(e);
                this.config.consoleLogger.warn(message);
                throw new RuntimeException(message);
            }
            catch (Exception e)
            {
                final String message = "Internal server error: "
                    + StringUtils.chainExceptionMessages(e);
                this.config.consoleLogger.error(message);
                logger.error(message, e);
                throw new RuntimeException(message);
            }

            plogger.end();
            return result;
        } finally {
            plogger.reset();
        }
    }

    /**
     * Converts from {@link RawCluster}s to a data structure compatible with XML-RPC
     * serializer.
     */
    private void addSubclusters(List outputClusters, Vector result, StringBuffer tmpbuf)
    {
        for (Iterator i = outputClusters.iterator(); i.hasNext();)
        {
            RawCluster rawCluster = (RawCluster) i.next();

            if (rawCluster.getProperty(RawCluster.PROPERTY_JUNK_CLUSTER) != null)
            {
                continue;
            }
            else
            {
                // Get a description phrase for this cluster.
                List phrases = rawCluster.getClusterDescription();
                for (int j = 0; j < Math.min(2, phrases.size()); j++)
                {
                    if (j > 0) tmpbuf.append(", ");
                    tmpbuf.append((String) phrases.get(j));
                }
            }

            String clusterLabel = tmpbuf.toString();
            tmpbuf.setLength(0);

            // quite ugly, but Apache's XML-RPC recognizes structures only using
            // hashtables.
            Hashtable t = new Hashtable(3);
            // add label
            t.put("label", clusterLabel);
            // add documents in this cluster
            List docs = rawCluster.getDocuments();
            if (docs != null && docs.size() > 0)
            {
                Vector ids = new Vector(docs.size());
                for (Iterator x = docs.iterator(); x.hasNext();)
                {
                    ids.add(((RawDocument) x.next()).getId());
                }
                t.put("documents", ids);
            }
            // add subclusters
            List subs = rawCluster.getSubclusters();
            Vector subsConverted = new Vector();
            if (subs != null && subs.size() > 0)
            {
                addSubclusters(subs, subsConverted, tmpbuf);
            }
            t.put("subclusters", subsConverted);

            // append this cluster to the parent
            result.add(t);
        }
    }

    /**
     * Echo method to test if XML-RPC is operational.
     * 
     * @param in Any string.
     * @return Returns an identical string.
     */
    public final String doEcho(String in)
    {
        return in;
    }
}
