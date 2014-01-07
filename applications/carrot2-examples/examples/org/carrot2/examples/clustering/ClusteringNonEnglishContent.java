
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2014, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples.clustering;

import java.util.List;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.examples.ConsoleFormatter;
import org.carrot2.examples.SampleDocumentData;
import org.carrot2.source.google.GoogleDocumentSource;
import org.carrot2.source.microsoft.Bing3WebDocumentSource;
import org.carrot2.source.microsoft.Bing3WebDocumentSourceDescriptor;
import org.carrot2.source.microsoft.MarketOption;
import org.carrot2.text.clustering.MultilingualClusteringDescriptor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * [[[start:clustering-non-english-content-intro]]]
 * <div>
 * <p>
 * This example shows how to cluster non-English content. By default Carrot2 assumes that
 * the documents provided for clustering are written in English. When clustering content
 * written in some different language, it is important to indicate the language to
 * Carrot2, so that it can use the lexical resources (stop words, tokenizer, stemmer)
 * appropriate for that language.
 * </p>
 * <p>
 * There are two ways to indicate the desired clustering language to Carrot2:
 * </p>
 * <ol>
 * <li>By setting the language of each document in their
 * {@link org.carrot2.core.Document#LANGUAGE} field. The language does not necessarily
 * have to be the same for all documents on the input, Carrot2 can handle multiple
 * languages in one document set as well. Please see the
 * {@link org.carrot2.text.clustering.MultilingualClustering#languageAggregationStrategy}
 * attribute for more details.</li>
 * <li>By setting the fallback language. For documents with undefined
 * {@link org.carrot2.core.Document#LANGUAGE} field, Carrot2 will assume the some fallback
 * language, which is English by default. You can change the fallback language by setting
 * the {@link org.carrot2.text.clustering.MultilingualClustering#defaultLanguage}
 * attribute.</li>
 * </ol>
 * Additionally, a number of document sources automatically set the
 * {@link org.carrot2.core.Document#LANGUAGE} of documents they produce based on their
 * specific language-related attributes. Currently, three documents support this scenario:
 * <ol>
 * <li>{@link org.carrot2.source.microsoft.Bing3WebDocumentSource} through the
 * {@link org.carrot2.source.microsoft.Bing3WebDocumentSource#market} attribute,</li>
 * <li>{@link org.carrot2.source.etools.EToolsDocumentSource} through the
 * {@link org.carrot2.source.etools.EToolsDocumentSource#language} attribute.</li>
 * </ol>
 * For the document sources that do not set the documents' language automatically, the
 * easiest way to set the clustering language is through the
 * {@link org.carrot2.text.clustering.MultilingualClustering#defaultLanguage} attribute.
 * </div>
 * [[[end:clustering-non-english-content-intro]]]
 */
public class ClusteringNonEnglishContent
{
    @SuppressWarnings("unchecked")
    public static void main(String [] args)
    {
        // [[[start:clustering-non-english-content]]]
        /*
         * We use a Controller that reuse instances of Carrot2 processing components 
         * and caches results produced by document sources.
         */
        final Controller controller = ControllerFactory.createCachingPooling(IDocumentSource.class);

        /*
         * In the first call, we'll cluster a document list, setting the language for each
         * document separately.
         */
        final List<Document> documents = Lists.newArrayList();
        for (Document document : SampleDocumentData.DOCUMENTS_DATA_MINING)
        {
            documents.add(new Document(document.getTitle(), document.getSummary(),
                document.getContentUrl(), LanguageCode.ENGLISH));
        }

        final Map<String, Object> attributes = Maps.newHashMap();
        CommonAttributesDescriptor.attributeBuilder(attributes)
            .documents(documents);
        final ProcessingResult englishResult = controller.process(
            attributes, LingoClusteringAlgorithm.class);
        ConsoleFormatter.displayResults(englishResult);

        /*
         * In the second call, we will fetch results for a Chinese query from Bing,
         * setting explicitly the Bing's specific language attribute. Based on that
         * attribute, the document source will set the appropriate language for each
         * document.
         */
        attributes.clear();
        
        CommonAttributesDescriptor.attributeBuilder(attributes)
            .query("聚类" /* clustering? */)
            .results(100);

        Bing3WebDocumentSourceDescriptor.attributeBuilder(attributes)
            .market(MarketOption.CHINESE_CHINA);
        Bing3WebDocumentSourceDescriptor
            .attributeBuilder(attributes)
                .appid(BingKeyAccess.getKey()); // use your own ID here!

        final ProcessingResult chineseResult = controller.process(attributes,
            Bing3WebDocumentSource.class, LingoClusteringAlgorithm.class);
        ConsoleFormatter.displayResults(chineseResult);

        /*
         * In the third call, we will fetch results for the same Chinese query from
         * Google. As Google document source does not have its specific attribute for
         * setting the language, it will not set the documents' language for us. To make
         * sure the right lexical resources are used, we will need to set the
         * MultilingualClustering.defaultLanguage attribute to Chinese on our own.
         */
        attributes.clear();
        
        CommonAttributesDescriptor.attributeBuilder(attributes)
            .query("聚类" /* clustering? */)
            .results(100);

        MultilingualClusteringDescriptor.attributeBuilder(attributes)
            .defaultLanguage(LanguageCode.CHINESE_SIMPLIFIED);

        final ProcessingResult chineseResult2 = controller.process(attributes,
            GoogleDocumentSource.class, LingoClusteringAlgorithm.class);
        ConsoleFormatter.displayResults(chineseResult2);
        // [[[end:clustering-non-english-content]]]
    }
}
