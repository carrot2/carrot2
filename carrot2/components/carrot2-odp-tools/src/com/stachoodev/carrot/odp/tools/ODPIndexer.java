/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.odp.tools;

import java.io.*;
import java.util.*;

import com.dawidweiss.carrot.util.common.*;
import com.stachoodev.carrot.odp.*;
import com.stachoodev.carrot.odp.index.*;
import com.stachoodev.util.common.*;

/**
 * A tool that creates all indexes registered with
 * {@link com.stachoodev.carrot.odp.index.AllKnownTopicIndexBuilders}for given
 * ODP content file.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ODPIndexer implements TopicIndexBuilderListener
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
    public ODPIndexer(String indexDataLocation)
    {
        this.indexDataLocation = indexDataLocation;
    }

    /**
     * @param maxDepth
     * @throws IOException
     */
    public void index(String contentFileLocation, Map properties)
        throws IOException
    {
        InputStream odpData = new FileInputStream(contentFileLocation);
        index(odpData, properties);
    }

    /**
     * @param odpData
     * @throws IOException
     */
    public void index(InputStream odpData) throws IOException
    {
        index(odpData, new HashMap());
    }

    /**
     * @param odpData
     * @throws IOException
     */
    public void index(InputStream odpData, Map properties) throws IOException
    {
        // Create the primary index first
        PrimaryTopicIndexBuilder primaryIndexBuilder = AllKnownTopicIndexBuilders
            .getPrimaryTopicIndexBuilder();
        PropertyHelper.setProperties(primaryIndexBuilder, properties);
        if (primaryIndexBuilder instanceof ObservableTopicIndexBuilder)
        {
            ((ObservableTopicIndexBuilder) primaryIndexBuilder)
                .addTopicIndexBuilderListener(this);
        }

        start = System.currentTimeMillis();
        PrimaryTopicIndex primaryIndex = primaryIndexBuilder.create(odpData,
            indexDataLocation);
        long stop = System.currentTimeMillis();

        displayProgressMessage(stop);

        odpData.close();

        // Serialize the primary index
        ODPIndex.getTopicIndexSerializer().serialize(
            primaryIndex,
            indexDataLocation + System.getProperty("file.separator")
                + ODPIndex.PRIMARY_TOPIC_INDEX_NAME);

        // Now create the rest of the indices required by the ODPIndex
        List indexNames = ODPIndex.getTopicIndexNames();
        for (Iterator iter = indexNames.iterator(); iter.hasNext();)
        {
            String name = (String) iter.next();
            TopicIndexBuilder topicIndexBuilder = AllKnownTopicIndexBuilders
                .getTopicIndexBuilder(name);
            if (topicIndexBuilder == null)
            {
                System.err.println("WARNING: no builder found for index '"
                    + name + "'");
                continue;
            }

            TopicIndex topicIndex = topicIndexBuilder.create(primaryIndex);
            ODPIndex.getTopicIndexSerializer()
                .serialize(
                    topicIndex,
                    indexDataLocation + System.getProperty("file.separator")
                        + name);
        }
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
     */
    public static void main(String [] args) throws IOException
    {
        if (args.length != 2 && args.length != 3)
        {
            System.out
                .println("Usage: ODPIndexer odp-raw-rdf-file odp-index-dir [max-depth]");
            return;
        }

        Map properties = new HashMap();
        if (args.length == 3)
        {
            properties.put(CatidPrimaryTopicIndexBuilder.PROPERTY_MAX_DEPTH,
                Integer.valueOf(args[2]));
        }

        ODPIndexer odpIndexer = new ODPIndexer(args[1]);

        odpIndexer.setProgressIndication(true);
        odpIndexer.index(args[0], properties);
    }
}