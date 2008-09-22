package org.carrot2.examples.clustering;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.source.microsoft.MicrosoftLiveDocumentSource;
import org.carrot2.util.attribute.AttributeUtils;

/**
 * This example shows how to set up and use {@link CachingController}, a production-grade
 * controller for running your clustering queries. This example assumes you are familiar
 * with {@link ClusteringDataFromDocumentSources} and {@link ClusteringDocumentList}
 * examples.
 */
public class UsingCachingController
{
    @SuppressWarnings(
    {
        "unused", "unchecked"
    })
    public static void main(String [] args)
    {
        /*
         * Caching controller caches instances of components in between requests and
         * reuses these instances when possible. An internal pool of instances is created
         * and can be configured arbitrarily to suit your needs (either an unbounded pool
         * or a bounded pool).
         */

        /*
         * Caching controller is always <b>thread-safe</b>, that is every processing
         * takes place on an independent set of component instances by a single thread.
         */

        /*
         * The first step is to pass classes of components that will be used during
         * processing. We will use Microsoft Live and Lingo clustering algorithm for this.
         */
        CachingController controller = new CachingController(
            MicrosoftLiveDocumentSource.class, LingoClusteringAlgorithm.class);

        /*
         * Perform a one-time initialization attributes (marked with @Init annotation).
         * For the caching controller these values are injected (and restored) for each
         * pooled component instance. Note the use of init method that accepts
         * component configuration and identifiers -- these identifiers are used later
         * on to drive queries.
         */
        Map<String, Object> initAttrs = new HashMap<String, Object>();
        initAttrs.put(AttributeUtils.getKey(MicrosoftLiveDocumentSource.class, "appid"),
            MicrosoftLiveDocumentSource.CARROTSEARCH_APPID);

        controller.init(initAttrs, 
            new ProcessingComponentConfiguration(MicrosoftLiveDocumentSource.class, "live"),
            new ProcessingComponentConfiguration(LingoClusteringAlgorithm.class, "lingo"));

        /*
         * The controller is now ready to perform queries. To show that the documents from
         * the document input are cached, we will perform the same query a few times and print
         * processing times to the output. The first query should take significantly longer
         * than subsequent queries (even taking into account JVM warm-ups etc.).
         */
        System.out.println("Query times: ");
        for (int i = 0; i < 10; i++)
        {
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put(AttributeNames.QUERY, "data mining");
            attributes.put(AttributeNames.RESULTS, 100);

            final long start = System.currentTimeMillis(); 
            ProcessingResult result = controller.process(attributes, "live", "lingo");
            final long duration = System.currentTimeMillis() - start;

            System.out.println(String.format("\t%+10.3f", duration/1000.0f));
        }
    }
}
