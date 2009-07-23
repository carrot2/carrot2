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
import org.carrot2.source.boss.*;
import org.carrot2.source.etools.EToolsDocumentSource;
import org.carrot2.source.microsoft.MicrosoftLiveDocumentSource;
import org.carrot2.text.linguistic.LanguageCode;

/**
 * This example shows how to cluster content in non-English languages. By default Carrot2
 * algorithms assume the content is in English and use the tokenizer, stemmer and stop
 * words appropriate for that language. There are two ways of performing non-English
 * clustering in Carrot2:
 * <ol>
 * <li>Set the {@link AttributeNames#ACTIVE_LANGUAGE} attribute to the appropriate value
 * from {@link LanguageCode}, which will make the clustering algorithm use the tokenizer,
 * stemmer and lexical resources dedicated to that language</li>
 * <li>When using a document source that can return content in different languages,
 * {@link AttributeNames#ACTIVE_LANGUAGE} will be determined based on the source-specific
 * language attribute. Currently, three document sources support language choice:
 * <ol>
 * <li>{@link MicrosoftLiveDocumentSource} through the
 * {@link MicrosoftLiveDocumentSource#culture} attribute</li>
 * <li>{@link BossDocumentSource} through the {@link BossSearchService#languageAndRegion}
 * attribute</li>
 * <li>{@link EToolsDocumentSource} through the {@link EToolsDocumentSource#language}
 * attribute</li>
 * </ol>
 * </ol>
 * <p>
 * <strong>Note:</strong> As the tokenizer for Chinese is fairly expensive to create, for
 * best performance when clustering Chinese content, use {@link CachingController} that
 * can cache processing component instances.
 * </p>
 */
public class ClusteringNonEnglishContent
{
    @SuppressWarnings("unchecked")
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
         * In the first call, we will fetch Chinese search results from MSN Live, but as
         * we don't explicitly set the document source's language to Chinese, we'll need
         * to provide the active language attribute.
         */
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(AttributeNames.QUERY, "聚类"); // clustering?
        attributes.put(AttributeNames.RESULTS, 100);
        attributes.put(AttributeNames.ACTIVE_LANGUAGE, LanguageCode.CHINESE_SIMPLIFIED);

        /*
         * Perform clustering and display results.
         */
        final ProcessingResult chineseResult = controller.process(attributes,
            MicrosoftLiveDocumentSource.class, LingoClusteringAlgorithm.class);
        ConsoleFormatter.displayResults(chineseResult);

        /*
         * In the second call, we will fetch German search results from eTools, and
         * explicitly instruct the document source to return results in German. In this
         * case, we don't need to set the active language attribute because the document
         * source will set it for us accordingly.
         */
        attributes.clear();
        attributes.put(AttributeNames.QUERY, "bundestag");
        attributes.put(AttributeNames.RESULTS, 100);
        attributes.put("EToolsDocumentSource.language", EToolsDocumentSource.Language.GERMAN);
        final ProcessingResult germanResult = controller.process(attributes,
            EToolsDocumentSource.class, LingoClusteringAlgorithm.class);
        ConsoleFormatter.displayResults(germanResult);

    }
}
