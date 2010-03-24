
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples.core;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.examples.SampleDocumentData;
import org.carrot2.examples.clustering.ClusteringDataFromDocumentSources;

/**
 * This example shows how to save clustering results to XML.
 * <p>
 * It is assumed that you are familiar with {@link ClusteringDataFromDocumentSources}
 * example.
 * </p>
 */
public class SavingResultsToXml
{
    public static void main(String [] args) throws Exception
    {
        // Let's fetch some results from MSN first
        final Controller controller = ControllerFactory.createSimple();
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(AttributeNames.DOCUMENTS, SampleDocumentData.DOCUMENTS_DATA_MINING);
        attributes.put(AttributeNames.QUERY, "data mining");

        final ProcessingResult result = controller.process(attributes,
            LingoClusteringAlgorithm.class);

        // Now, we can serialize the entire result to XML like this
        result.serialize(System.out);
        System.out.println();

        // Optionally, we can choose whether we want to serialize documents and clusters
        result.serialize(System.out, 
            false /* don't save documents */,
            true /* save clusters */);
    }
}
