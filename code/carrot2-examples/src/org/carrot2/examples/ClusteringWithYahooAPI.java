/**
 * 
 */
package org.carrot2.examples;

import java.util.*;

import org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.core.controller.SimpleController;
import org.carrot2.source.yahoo.YahooDocumentSource;

/**
 * An example showing how to query Yahoo! for search results
 * and cluster them dynamically.
 */
public class ClusteringWithYahooAPI
{
    @SuppressWarnings("unchecked")
    public static void main(String [] args)
    {
        final SimpleController controller = new SimpleController();

        // Initialization parameters for components.
        final Map<String, Object> parameters = new HashMap<String, Object>();
        
        // Runtime attributes passed between components.
        final Map<String, Object> attributes = new HashMap<String, Object>();

        ProcessingResult result = controller.process(parameters, attributes, 
                YahooDocumentSource.class, ByUrlClusteringAlgorithm.class);

        ExampleUtils.displayResults(result);
    }
}
