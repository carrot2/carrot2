
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples.clustering;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.examples.ConsoleFormatter;
import org.carrot2.source.microsoft.CultureInfo;
import org.carrot2.source.microsoft.MicrosoftLiveDocumentSource;
import org.carrot2.text.analysis.ChineseAnalyzer;
import org.carrot2.text.linguistic.LanguageCode;

/**
 * This example shows how to cluster Chinese content. 
 */
public class ClusteringChineseContent
{
    public static void main(String [] args)
    {
        /*
         * We use a CachingController here to reuse instances of Carrot2 processing 
         * components. This is especially important when the components are expensive
         * to create, which is the case with ChineseAnalyzer.
         */
        final CachingController controller = new CachingController();

        /*
         * Initialize the controller setting the clustering language and analyzer
         * to Chinese by default for all further processing.
         */
        final Map<String, Object> initAttributes = new HashMap<String, Object>();
        initAttributes.put(AttributeNames.ACTIVE_LANGUAGE, LanguageCode.CHINESE);
        initAttributes.put("Tokenizer.analyzer", ChineseAnalyzer.class);
        controller.init(initAttributes);

        /*
         * In this example we fetch Chinese search results from a search engine, but
         * clustering should also work for any source of documents as long as the 
         * appropriate language and analyzer are set (see above). 
         */
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(AttributeNames.QUERY, "聚类"); // clustering?
        attributes.put(AttributeNames.RESULTS, 100);
        attributes.put("MicrosoftLiveDocumentSource.culture", CultureInfo.CHINESE_CHINA);

        /*
         * Perform clustering and display results.
         */
        final ProcessingResult result = controller.process(attributes,
            MicrosoftLiveDocumentSource.class, LingoClusteringAlgorithm.class);
        ConsoleFormatter.displayResults(result);
    }
}
