/*
 * ODPIndexer.java
 * 
 * Created on 2004-06-26
 */
package com.stachoodev.carrot.odp.tools;

import java.io.*;
import java.util.*;

import com.stachoodev.carrot.odp.*;
import com.stachoodev.carrot.odp.index.*;

/**
 * @author stachoo
 */
public class ODPIndexer implements TopicIndexBuilderListener
{
    /** The number of topics indexed so far */
    private int topicsIndexed;

    /** Location of the ODP content file */
    private String contentFileLocation;

    /** Destination for the index files */
    private String indexDataLocation;

    /** Start time */
    private long start;

    /**
     * @param contentFileLocation
     */
    public ODPIndexer(String contentFileLocation, String indexDataLocation)
    {
        this.contentFileLocation = contentFileLocation;
        this.indexDataLocation = indexDataLocation;
    }

    /**
     * @throws IOException
     *  
     */
    public void index() throws IOException
    {
        // Create the primary index first
        InputStream odpData = new FileInputStream(contentFileLocation);

        PrimaryTopicIndexBuilder primaryIndexBuilder = AllKnownTopicIndexBuilders
            .getPrimaryTopicIndexBuilder();
        if (primaryIndexBuilder instanceof ObservableTopicIndexBuilder)
        {
            ((ObservableTopicIndexBuilder) primaryIndexBuilder)
                .addTopicIndexBuilderListener(this);
        }

        start = System.currentTimeMillis();
        PrimaryTopicIndex primaryIndex = primaryIndexBuilder.create(odpData,
            indexDataLocation);
        long stop = System.currentTimeMillis();

        System.out.println("Indexed " + topicsIndexed + " topics in "
            + ((stop - start) / 1000.0) + " seconds ("
            + (topicsIndexed / ((stop - start) / 1000.0)) + " topics/s).");

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
            ODPIndex.getTopicIndexSerializer().serialize(
                topicIndex,
                indexDataLocation + System.getProperty("file.separator")
                    + name);
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
            System.out.println("Indexed " + topicsIndexed + " topics in "
                + ((stop - start) / 1000.0) + " seconds ("
                + (topicsIndexed / ((stop - start) / 1000.0)) + " topics/s).");
        }
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String [] args) throws IOException
    {
        ODPIndexer odpIndexer = new ODPIndexer(args[0], args[1]);

        odpIndexer.index();
    }
}