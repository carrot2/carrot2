
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

package org.carrot2.core.impl;

import java.io.*;
import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;

/**
 * This component acts as an interceptor in the processing chain and saves the query, documents, clusters and other
 * information to a file or a stream.
 * 
 * @see #PARAM_OUTPUT_FILE
 * @see #PARAM_OUTPUT_STREAM
 * @see #PARAM_SAVE_CLUSTERS
 * @see #PARAM_SAVE_DOCUMENTS
 * @see #PARAM_CLOSE_STREAM
 */
public abstract class SaveFilterComponentBase extends LocalFilterComponentBase implements RawClustersConsumer,
    RawClustersProducer, RawDocumentsProducer, RawDocumentsConsumer
{
    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = toSet(RawClustersProducer.class);

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = toSet(new Object []
    {
        RawClustersConsumer.class, RawClustersProducer.class, RawDocumentsConsumer.class
    });

    /**
     * Capabilities required from the next component in the chain (none, but {@link RawClustersConsumer} and
     * {@link RawClustersProducer} are accepted.
     */
    private final static Set CAPABILITIES_SUCCESSOR = Collections.EMPTY_SET;

    /** Specifies the file to which the data should be saved. */
    public static final String PARAM_OUTPUT_FILE = "output-file";

    /** Specifies the output stream to which the data should be saved. */
    public static final String PARAM_OUTPUT_STREAM = "output-stream";

    /**
     * If set, the stream is closed upon end of processing. Value of this parameter must be of type {@link Boolean}.
     * Defaults to <code>true</code> if not present.
     */
    public static final String PARAM_CLOSE_STREAM = "close-stream";

    /**
     * Specifies whether clusters should also be saved. Value of this parameter must be of type {@link Boolean}.
     * Defaults to <code>false</code> if not present.
     */
    public static final String PARAM_SAVE_CLUSTERS = "save-clusters";

    /**
     * Specifies whether documents should be saved. Value of this parameter must be of type {@link Boolean}. Defaults
     * to <code>true</code> if not present.
     */
    public static final String PARAM_SAVE_DOCUMENTS = "save-documents";

    /** Raw clusters consumer. */
    private RawClustersConsumer rawClustersConsumer;

    /** Raw documents consumer. */
    private RawDocumentsConsumer rawDocumentsConsumer;

    /** The stream we will be saving to. */
    private OutputStream outputStream;

    /** Automatically close the output stream upon completion of a request. */
    private boolean closeStream;

    /** Temporal storage for clusters. */
    private final ArrayList rawClusters = new ArrayList();

    /** Temporal storage for documents. */
    private final ArrayList rawDocuments = new ArrayList();

    /** Reference to the current request context. */
    protected RequestContext requestContext;

    /**
     * Instances only from within the package.
     */
    SaveFilterComponentBase()
    {
    }

    /*
     * 
     */
    public final Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    /*
     * 
     */
    public final Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    /*
     * 
     */
    public final Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

    /*
     * 
     */
    public final void setNext(LocalComponent next)
    {
        super.setNext(next);

        if (next instanceof RawClustersConsumer)
        {
            rawClustersConsumer = (RawClustersConsumer) next;
        }
        if (next instanceof RawDocumentsConsumer)
        {
            rawDocumentsConsumer = (RawDocumentsConsumer) next;
        }
    }

    /**
     * Implementation of {@link RawClustersConsumer#addCluster(RawCluster)}.
     */
    public void addCluster(RawCluster cluster) throws ProcessingException
    {
        if (outputStream != null)
        {
            rawClusters.add(cluster);
        }

        if (rawClustersConsumer != null)
        {
            rawClustersConsumer.addCluster(cluster);
        }
    }

    /**
     * Implementation of {@link RawDocumentsConsumer#addDocument(RawDocument)}.
     */
    public void addDocument(RawDocument doc) throws ProcessingException
    {
        if (outputStream != null)
        {
            rawDocuments.add(doc);
        }

        if (this.rawDocumentsConsumer != null)
        {
            rawDocumentsConsumer.addDocument(doc);
        }
    }

    /*
     * 
     */
    public final void startProcessing(RequestContext requestContext) throws ProcessingException
    {
        super.startProcessing(requestContext);

        this.requestContext = requestContext;

        // Parse and determine parameters.
        final Boolean closeStreamParam = (Boolean) requestContext.getRequestParameters().get(PARAM_CLOSE_STREAM);
        this.closeStream = (closeStreamParam != null ? closeStreamParam.booleanValue() : true);

        final OutputStream os = (OutputStream) requestContext.getRequestParameters().get(PARAM_OUTPUT_STREAM);
        final File outputFile = (File) requestContext.getRequestParameters().get(PARAM_OUTPUT_FILE);
        if (os != null && outputFile != null)
        {
            throw new ProcessingException(PARAM_OUTPUT_STREAM + " or " + PARAM_OUTPUT_FILE + " request"
                + " context parameters are required (mutually exclusive).");
        }

        if (outputFile != null)
        {
            if (outputFile.isDirectory())
            {
                throw new ProcessingException("Output file must not be a directory: " + outputFile.getAbsolutePath());
            }
            try
            {
                // force stream closing (we opened it).
                outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
                this.closeStream = true;
            }
            catch (FileNotFoundException e)
            {
                throw new ProcessingException(e);
            }
        }
        else if (os != null)
        {
            outputStream = os;
        }
        else
        {
            // do nothing, passthrough.
        }
    }

    /*
     * 
     */
    public final void endProcessing() throws ProcessingException
    {
        try
        {
            if (outputStream != null)
            {
                // determine options.
                final Boolean saveClustersParam = (Boolean) requestContext.getRequestParameters().get(
                    PARAM_SAVE_CLUSTERS);
                final Boolean saveDocumentsParam = (Boolean) requestContext.getRequestParameters().get(
                    PARAM_SAVE_DOCUMENTS);

                final boolean saveDocuments = saveDocumentsParam != null ? saveDocumentsParam.booleanValue() : true;
                final boolean saveClusters = saveClustersParam != null ? saveClustersParam.booleanValue() : false;

                // If documents save requested and no documents passed, try to collect them from the
                // clusters hierarchy.
                ArrayList documents = this.rawDocuments;
                if (saveDocuments && documents.size() == 0)
                {
                    documents = collectRawDocuments();
                }

                endProcessing0(outputStream, this.rawDocuments, this.rawClusters, saveDocuments, saveClusters);
            }
        }
        finally
        {
            super.endProcessing();
        }
    }

    /**
     * Override this method and end processing here (write to the {@link #outputStream}).
     */
    protected abstract void endProcessing0(OutputStream os, ArrayList documents, ArrayList clusters,
        boolean saveDocuments, boolean saveClusters) throws ProcessingException;

    /**
     * 
     */
    public final void flushResources()
    {
        super.flushResources();

        requestContext = null;
        rawClustersConsumer = null;

        if (this.closeStream)
        {
            try
            {
                if (this.outputStream != null) this.outputStream.close();
            }
            catch (IOException e)
            {
                // Ignore I/O exceptions here.
            }
        }

        this.closeStream = false;
        outputStream = null;

        rawClusters.clear();
        rawDocuments.clear();

        this.flushResources0();
    }

    /**
     * Override this method if you need extra resource flushing in the subclass.
     */
    protected void flushResources0()
    {
        // empty by default.
    }

    /**
     * Collects {@link RawDocument}s from the structure of {@link RawCluster}s. This may induce some performance
     * penalty.
     */
    private ArrayList collectRawDocuments()
    {
        final Set documentSet = new HashSet();
        for (Iterator it = rawClusters.iterator(); it.hasNext();)
        {
            RawCluster rawCluster = (RawCluster) it.next();
            collectRawDocuments(rawCluster, documentSet);
        }

        final ArrayList orderedDocuments = new ArrayList(documentSet);
        Collections.sort(orderedDocuments, new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                RawDocument docA = (RawDocument) o1;
                RawDocument docB = (RawDocument) o2;
                Integer seqA = (Integer) docA.getProperty(RawDocumentEnumerator.DOCUMENT_SEQ_NUMBER);
                Integer seqB = (Integer) docB.getProperty(RawDocumentEnumerator.DOCUMENT_SEQ_NUMBER);

                if (seqA == null)
                {
                    return -1;
                }

                if (seqB == null)
                {
                    return 1;
                }

                return seqA.intValue() - seqB.intValue();
            }
        });

        return orderedDocuments;
    }

    /**
     * 
     */
    private void collectRawDocuments(RawCluster rawCluster, Set documents)
    {
        documents.addAll(rawCluster.getDocuments());

        final List subclusters = rawCluster.getSubclusters();
        if (subclusters != null)
        {
            for (Iterator it = subclusters.iterator(); it.hasNext();)
            {
                RawCluster subcluster = (RawCluster) it.next();
                collectRawDocuments(subcluster, documents);
            }
        }
    }
}
