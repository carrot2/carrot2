
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

package org.carrot2.tools.odp.tools;

import java.io.*;
import java.util.*;

import org.carrot2.input.odp.ODPIndex;
import org.carrot2.input.odp.TopicSerializer;
import org.carrot2.input.odp.index.PrimaryTopicIndex;
import org.carrot2.tools.odp.common.ObservableTopicIndexBuilder;
import org.carrot2.tools.odp.common.TopicIndexBuilderListener;
import org.carrot2.tools.odp.index.*;
import org.carrot2.util.PropertyHelper;
import org.carrot2.util.StringUtils;

/**
 * A tool that creates all indexes registered with
 * {@link AllKnownTopicIndexBuilders} for given ODP content file.
 * 
 * TODO: topic filtering - too small topics, letter catetories (e.g. Domains/A)
 * TODO: switch from input arguments to properties
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

    public ODPIndexer(String indexDataLocation)
    {
        this.indexDataLocation = indexDataLocation;
    }

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
        PrimaryTopicIndexBuilder primaryIndexBuilder = AllKnownTopicIndexBuilders
            .getPrimaryTopicIndexBuilder();
        PropertyHelper.setProperties(primaryIndexBuilder, properties);
        if (primaryIndexBuilder instanceof ObservableTopicIndexBuilder)
        {
            ((ObservableTopicIndexBuilder) primaryIndexBuilder)
                .addTopicIndexBuilderListener(this);
        }

        // Initialise the topic serializer
        TopicSerializer topicSerializer = ODPIndex.getTopicSerializer();
        topicSerializer.initialize(indexDataLocation);

        // Initialise topic index builders
        Map topicIndexBuilders = AllKnownTopicIndexBuilders
            .getTopicIndexBuilders();
        for (Iterator iter = topicIndexBuilders.values().iterator(); iter
            .hasNext();)
        {
            TopicIndexBuilder topicIndexBuilder = (TopicIndexBuilder) iter
                .next();
            topicIndexBuilder.initialize();
        }

        // Create the primary index and topic indices
        start = System.currentTimeMillis();
        PrimaryTopicIndex primaryIndex = primaryIndexBuilder.create(odpData,
            topicSerializer, topicIndexBuilders.values());
        long stop = System.currentTimeMillis();
        displayProgressMessage(stop);

        odpData.close();
        topicSerializer.dispose();

        // Serialize the primary index
        displayMessage("Serializing primary topic index...");
        OutputStream out = new FileOutputStream(indexDataLocation
            + System.getProperty("file.separator")
            + ODPIndex.PRIMARY_TOPIC_INDEX_NAME);
        primaryIndex.serialize(out);
        out.close();

        // Serialize the topic indices
        for (Iterator iter = topicIndexBuilders.keySet().iterator(); iter
            .hasNext();)
        {
            String name = (String) iter.next();
            displayMessage("Serializing topic index: " + name + "...");

            TopicIndexBuilder topicIndexBuilder = (TopicIndexBuilder) topicIndexBuilders
                .get(name);

            out = new FileOutputStream(indexDataLocation
                + System.getProperty("file.separator") + name);
            topicIndexBuilder.getTopicIndex().serialize(out);
            out.close();
        }

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
        if (topicsIndexed % 1000 == 0)
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
        if (args.length != 2 && args.length != 3)
        {
            System.out
                .println("Usage: ODPIndexer odp-raw-rdf-file odp-index-dir");
            return;
        }

        Map properties = new HashMap();

        ODPIndexer odpIndexer = new ODPIndexer(args[1]);

        odpIndexer.setProgressIndication(true);
        odpIndexer.index(args[0], properties);
    }
}