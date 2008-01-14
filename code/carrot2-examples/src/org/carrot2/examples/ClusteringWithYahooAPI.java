/**
 * 
 */
package org.carrot2.examples;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.controller.SimpleController;
import org.carrot2.source.yahoo.YahooDocumentSource;
import org.carrot2.source.yahoo.YahooNewsSearchService;

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

        parameters.put("query", "data mining");
        parameters.put("results", 200);

        // Runtime attributes passed between components.
        final Map<String, Object> attributes = new HashMap<String, Object>();

        ProcessingResult result = controller.process(parameters, attributes, 
                YahooDocumentSource.class, ByUrlClusteringAlgorithm.class);

        ExampleUtils.displayResults(result);
        
        //
        // Fetching from Yahoo! News.
        //
        parameters.put(YahooDocumentSource.class.getName() + ".service", 
            YahooNewsSearchService.class);
        parameters.put("query", "iraq");
        parameters.put("results", 50);

        result = controller.process(parameters, attributes, 
            YahooDocumentSource.class, ByUrlClusteringAlgorithm.class);
        ExampleUtils.displayResults(result);
    }
}
