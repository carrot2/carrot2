/**
 * 
 */
package org.carrot2.examples;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
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

        // Runtime attributes passed between components.
        final Map<String, Object> attributes = new HashMap<String, Object>();

        attributes.put(AttributeNames.QUERY, "data mining");
        attributes.put(AttributeNames.RESULTS, 200);

        ProcessingResult result = controller.process(attributes, 
                YahooDocumentSource.class, ByUrlClusteringAlgorithm.class);

        ExampleUtils.displayResults(result);
        
        //
        // Fetching from Yahoo! News.
        //
        attributes.put(YahooDocumentSource.class.getName() + ".service", 
            YahooNewsSearchService.class);

        attributes.put(AttributeNames.QUERY, "iraq");
        attributes.put(AttributeNames.RESULTS, 50);
        
        result = controller.process(attributes, 
            YahooDocumentSource.class, ByUrlClusteringAlgorithm.class);
        ExampleUtils.displayResults(result);
    }
}
