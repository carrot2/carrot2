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
import org.carrot2.source.boss.BossSearchService;
import org.carrot2.source.microsoft.CultureInfo;
import org.carrot2.source.microsoft.MicrosoftLiveDocumentSource;
import org.carrot2.text.linguistic.LanguageCode;

/**
 * This example shows how to cluster content in non-English languages, e.g. Chinese. The
 * key to clustering content in other languages is to set the
 * {@link AttributeNames#ACTIVE_LANGUAGE} to the appropriate value from
 * {@link LanguageCode}, which will make the clustering algorithm use the tokenizer,
 * stemmer and lexical resources dedicated to that language.
 * <p>
 * When using a document source that can return content in different languages, such as
 * {@link MicrosoftLiveDocumentSource} or BossDocumentSource, the appropriate
 * {@link AttributeNames#ACTIVE_LANGUAGE} will be determined based on the source-specific
 * language attribute, e.g. {@link MicrosoftLiveDocumentSource#culture} or
 * {@link BossSearchService#languageAndRegion}.
 * </p>
 */
public class ClusteringNonEnglishContent
{
    public static void main(String [] args)
    {
        /*
         * We use a CachingController here to reuse instances of Carrot2 processing
         * components. This is especially important when the components are expensive to
         * create, which is the case with ChineseAnalyzer.
         */
        final CachingController controller = new CachingController(IDocumentSource.class);

        /*
         * No special initialization-time attributes in this example. 
         */
        final Map<String, Object> initAttributes = new HashMap<String, Object>();
        controller.init(initAttributes);

        /*
         * In the first call, we will fetch Chinese search results from a search engine,
         * and the engine will set the active language to Chinese automatically.
         */
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(AttributeNames.QUERY, "聚类"); // clustering?
        attributes.put(AttributeNames.RESULTS, 100);

        // MicrosoftLiveDocumentSource-specific attribute for setting the language
        attributes.put("MicrosoftLiveDocumentSource.culture", CultureInfo.CHINESE_CHINA);

        /*
         * Perform clustering and display results.
         */
        final ProcessingResult chineseResult = controller.process(attributes,
            MicrosoftLiveDocumentSource.class, LingoClusteringAlgorithm.class);
        controller.process(attributes, MicrosoftLiveDocumentSource.class,
            LingoClusteringAlgorithm.class);
        ConsoleFormatter.displayResults(chineseResult);
    }
}
