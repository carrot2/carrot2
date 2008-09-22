/**
 *
 */
package org.carrot2.examples.clustering;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.examples.ExampleUtils;
import org.carrot2.source.boss.*;
import org.carrot2.source.microsoft.MicrosoftLiveDocumentSource;
import org.carrot2.util.attribute.AttributeUtils;

/**
 * This example shows how to cluster {@link Document}s retrieved from
 * {@link DocumentSource} providers. There are a number of implementations of this
 * interface in the Carrot2 project, we will cluster Microsoft Live (Web search) and Yahoo
 * Boss (news search) here.
 * <p>
 * It is assumed that you are familiar with {@link ClusteringDocumentList} example.
 * 
 * @see UsingCachingController
 */
public class ClusteringDataFromDocumentSources
{
    @SuppressWarnings("unused")
    public static void main(String [] args)
    {
        /*
         * EXAMPLE 1: fetching documents from Microsoft Live, clustering with Lingo.
         * Attributes for the first query: query, number of results to fetch from the
         * source. Note that the API key defaults to the one assigned for the Carrot2
         * project. Please use your own key for production use.
         */
        SimpleController controller = new SimpleController();

        /*
         * One-time initialization attributes (marked with @Init annotation). For
         * BasicController these attributes are injected into component instances in each
         * processing round anyway (because instances are not reused), but for the
         * CachingController they will be injected only once for each pooled component
         * instance.
         */
        Map<String, Object> initAttrs = new HashMap<String, Object>();
        initAttrs.put(AttributeUtils.getKey(MicrosoftLiveDocumentSource.class, "appid"),
            MicrosoftLiveDocumentSource.CARROTSEARCH_APPID);
        controller.init(initAttrs);

        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(AttributeNames.QUERY, "data mining");
        attributes.put(AttributeNames.RESULTS, 100);

        ProcessingResult result = controller.process(attributes,
            MicrosoftLiveDocumentSource.class, LingoClusteringAlgorithm.class);

        ExampleUtils.displayResults(result);

        /*
         * EXAMPLE 2: fetching from Yahoo BOSS (news search), clustering with Lingo.
         * Attributes for the first query: query, number of results to fetch from the
         * source. Again, note the API key. Please use your own key for production use.
         */
        controller = new SimpleController();

        /*
         * One-time initialization attributes.
         */
        initAttrs = new HashMap<String, Object>();
        initAttrs.put(AttributeUtils.getKey(BossSearchService.class, "appid"),
            BossSearchService.CARROTSEARCH_APPID);

        /*
         * Boss document source is generic and can retrieve Web search, news and image
         * results. Pick the service to use by passing the right service implementation.
         */
        initAttrs.put(AttributeUtils.getKey(BossDocumentSource.class, "service"),
            BossNewsSearchService.class);

        controller.init(initAttrs);

        attributes = new HashMap<String, Object>();
        attributes.put(AttributeNames.QUERY, "war");
        attributes.put(AttributeNames.RESULTS, 50);

        result = controller.process(attributes, BossDocumentSource.class,
            LingoClusteringAlgorithm.class);

        ExampleUtils.displayResults(result);
    }
}
