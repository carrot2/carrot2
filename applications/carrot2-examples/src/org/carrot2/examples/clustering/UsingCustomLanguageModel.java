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

import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Tokenizer;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.examples.ConsoleFormatter;
import org.carrot2.source.etools.EToolsDocumentSource;
import org.carrot2.text.analysis.ExtendedWhitespaceTokenizer;
import org.carrot2.text.linguistic.*;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * This example shows how to perform clustering using a custom language model, including
 * stop words, stop labels and stemmer.
 */
public class UsingCustomLanguageModel
{
    public static void main(String [] args)
    {
        final CachingController controller = new CachingController(IDocumentSource.class);

        // We will pass our custom language model factory class as a initialization-time
        // attribute. It is preferred to passing it as a processing-time attribute
        // because it the instance created at initialization time is reused for all
        // further requests.
        final Map<String, Object> initAttributes = Maps.newHashMap();
        initAttributes.put("PreprocessingPipeline.languageModelFactory",
            CustomLanguageModelFactory.class);
        controller.init(initAttributes);

        // Cluster some data with Lingo and STC. Notice how the cluster quality degrades
        // when the stop word list is empty (especially for STC).
        clusterAndDisplayClusters(controller, LingoClusteringAlgorithm.class);
        clusterAndDisplayClusters(controller, STCClusteringAlgorithm.class);
    }

    /**
     * Clusters results for query "data minig" and displays the clusters.
     */
    private static void clusterAndDisplayClusters(final CachingController controller,
        final Class<? extends IClusteringAlgorithm> clusteringAlgorithm)
    {
        final Map<String, Object> processingAttributes = Maps.newHashMap();
        processingAttributes.put(AttributeNames.QUERY, "data mining");
        final ProcessingResult result = controller.process(processingAttributes,
            EToolsDocumentSource.class, clusteringAlgorithm);
        ConsoleFormatter.displayClusters(result.getClusters(), 0);
    }

    /**
     * A custom language model factory.
     */
    public static class CustomLanguageModelFactory implements ILanguageModelFactory
    {
        public ILanguageModel getLanguageModel(LanguageCode language)
        {
            // Here we always return the same language model, regardless of the requested
            // language. In your implementation you may want to return different models
            // based on the language, if needed.
            return new CustomLanguageModel();
        }

        /**
         * Custom language model implementation. This one uses some contrived algorithms
         * and stop words just to demonstrate how they work.
         */
        private static final class CustomLanguageModel implements ILanguageModel
        {
            private static final Set<? extends CharSequence> STOP_WORDS = ImmutableSet
                .of("text");

            public boolean isStopLabel(CharSequence formattedLabel)
            {
                return formattedLabel.length() <= 4;
            }

            public boolean isCommonWord(CharSequence word)
            {
                return STOP_WORDS.contains(word.toString());
            }

            public IStemmer getStemmer()
            {
                return new IStemmer()
                {
                    public CharSequence stem(CharSequence word)
                    {
                        // Some contrived stemming algorithm
                        return word.length() > 3 ? word.subSequence(0, word.length() - 2)
                            : null;
                    }
                };
            }
            

            public Tokenizer getTokenizer()
            {
                return new ExtendedWhitespaceTokenizer();
            }

            public LanguageCode getLanguageCode()
            {
                return null;
            }
        }
    }
}
