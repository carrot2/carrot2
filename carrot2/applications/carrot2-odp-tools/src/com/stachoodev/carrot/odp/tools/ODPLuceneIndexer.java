
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.stachoodev.carrot.odp.tools;

import java.io.*;
import java.util.*;

import com.dawidweiss.carrot.util.common.*;
import com.stachoodev.carrot.odp.common.*;
import com.stachoodev.carrot.odp.lucene.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ODPLuceneIndexer implements TopicIndexBuilderListener
{
    /** The number of topics indexed so far */
    private int topicsIndexed;

    /** Destination for the index files */
    private String indexDataLocation;

    /** Start time */
    private long start;

    /** Progress reporting flag */
    private boolean progressIndication;

    /**
     * @param contentFileLocation
     */
    public ODPLuceneIndexer(String indexDataLocation)
    {
        this.indexDataLocation = indexDataLocation;
    }

    /**
     * @param maxDepth
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void index(String contentFileLocation, Map properties)
        throws IOException, ClassNotFoundException
    {
        InputStream odpData = new FileInputStream(contentFileLocation);
        index(odpData, properties);
    }

    /**
     * @param odpData
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void index(InputStream odpData) throws IOException,
        ClassNotFoundException
    {
        index(odpData, new HashMap());
    }

    /**
     * @param odpData
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void index(InputStream odpData, Map properties) throws IOException,
        ClassNotFoundException
    {
        // Get the primary index builder
        LuceneIndexBuilder luceneIndexBuilder = new LuceneIndexBuilder();
        luceneIndexBuilder.addTopicIndexBuilderListener(this);

        // Create the primary index and topic indices
        start = System.currentTimeMillis();
        luceneIndexBuilder.index(odpData, indexDataLocation);
        long stop = System.currentTimeMillis();
        displayProgressMessage(stop);

        displayMessage("Closing index...");
        luceneIndexBuilder.close();
        odpData.close();
        displayMessage("Done");
    }

    /**
     * @param stop
     */
    private void displayProgressMessage(long stop)
    {
        if (progressIndication)
        {
            System.out.println("Indexed "
                + topicsIndexed
                + " topics in "
                + StringUtils.toString(new Double((stop - start) / 1000.0),
                    "#.##")
                + " seconds ("
                + StringUtils.toString(new Double(topicsIndexed
                    / ((stop - start) / 1000.0)), "#.##") + " topics/s).");
        }
    }

    /**
     * @param message
     */
    private void displayMessage(String message)
    {
        if (progressIndication)
        {
            System.out.println(message);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.index.TopicIndexBuilderListener#topicIndexed()
     */
    public void topicIndexed()
    {
        topicsIndexed++;
        long stop = System.currentTimeMillis();
        if (topicsIndexed % 100 == 0)
        {
            displayProgressMessage(stop);
        }
    }

    /**
     * Sets this ODPIndexer's <code>progressIndication</code>.
     * 
     * @param progressIndication
     */
    public void setProgressIndication(boolean progressIndication)
    {
        this.progressIndication = progressIndication;
    }

    /**
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void main(String [] args) throws IOException,
        ClassNotFoundException
    {
        String odpSrcFile = System.getProperty("odp.src.file");
        String luceneIndexDir = System.getProperty("odp.lucene.index.dir");

        if (odpSrcFile == null)
        {
            System.err
                .println("Please provide a path to an ODP RDF file in the 'odp.src.file' property");
            return;
        }

        if (luceneIndexDir == null)
        {
            System.err
                .println("Please provide a path for the Lucene index in the 'odp.lucene.index.dir' property");
            return;
        }

        Map properties = new HashMap();

        ODPLuceneIndexer odpIndexer = new ODPLuceneIndexer(luceneIndexDir);

        odpIndexer.setProgressIndication(true);
        odpIndexer.index(odpSrcFile, properties);
    }
}