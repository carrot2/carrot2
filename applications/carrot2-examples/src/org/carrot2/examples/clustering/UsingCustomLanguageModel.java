
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.examples.clustering;

import java.util.Map;
import java.util.Set;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.IClusteringAlgorithm;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.carrot2.examples.ConsoleFormatter;
import org.carrot2.examples.SampleDocumentData;
import org.carrot2.text.analysis.ExtendedWhitespaceTokenizer;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.ILanguageModel;
import org.carrot2.text.linguistic.ILanguageModelFactory;
import org.carrot2.text.linguistic.ILexicalData;
import org.carrot2.text.linguistic.IStemmer;
import org.carrot2.text.preprocessing.pipeline.BasicPreprocessingPipelineDescriptor;
import org.carrot2.text.util.MutableCharArray;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * This example shows how to perform clustering using a custom language model, including
 * stop words, stop labels and stemmer.
 */
public class UsingCustomLanguageModel
{
    public static void main(String [] args)
    {
        @SuppressWarnings("unchecked")
        final Controller controller = ControllerFactory.createCachingPooling(IDocumentSource.class);

        // We will pass our custom language model factory class as a initialization-time
        // attribute. It is preferred to passing it as a processing-time attribute
        // because it the instance created at initialization time is reused for all
        // further requests.
        Map<String, Object> attrs = Maps.newHashMap();
        BasicPreprocessingPipelineDescriptor.attributeBuilder(attrs)
            .languageModelFactory(CustomLanguageModelFactory.class);
        controller.init(attrs);

        // Cluster some data with Lingo and STC. Notice how the cluster quality degrades
        // when the stop word list is empty (especially for STC).
        clusterAndDisplayClusters(controller, LingoClusteringAlgorithm.class);
        clusterAndDisplayClusters(controller, STCClusteringAlgorithm.class);
    }

    /**
     * Clusters results for query "data mining" and displays the clusters.
     */
    private static void clusterAndDisplayClusters(final Controller controller,
        final Class<? extends IClusteringAlgorithm> clusteringAlgorithm)
    {
        final Map<String, Object> processingAttributes = Maps.newHashMap();

        CommonAttributesDescriptor.attributeBuilder(processingAttributes)
            .documents(Lists.newArrayList(SampleDocumentData.DOCUMENTS_DATA_MINING))
            .query("data mining");

        final ProcessingResult result = controller.process(processingAttributes, 
            clusteringAlgorithm);
        ConsoleFormatter.displayClusters(result.getClusters(), 0);
    }

    /**
     * A custom language model factory.
     */
    public static class CustomLanguageModelFactory implements ILanguageModelFactory
    {
        private static final Set<? extends CharSequence> STOP_WORDS = 
            ImmutableSet.of("text");

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
            

            public ITokenizer getTokenizer()
            {
                return new ExtendedWhitespaceTokenizer();
            }

            public LanguageCode getLanguageCode()
            {
                return null;
            }

            @Override
            public ILexicalData getLexicalData()
            {
                return new ILexicalData()
                {
                    @Override
                    public boolean isStopLabel(CharSequence formattedLabel)
                    {
                        return formattedLabel.length() <= 4;
                    }

                    @Override
                    public boolean isCommonWord(MutableCharArray word)
                    {
                        return STOP_WORDS.contains(word.toString());
                    }
                };
            }
        }
    }
}
