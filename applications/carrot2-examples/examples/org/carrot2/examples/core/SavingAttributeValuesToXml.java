
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

package org.carrot2.examples.core;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithmDescriptor;
import org.carrot2.matrix.factorization.LocalNonnegativeMatrixFactorizationFactory;
import org.carrot2.matrix.factorization.IterationNumberGuesser.FactorizationQuality;
import org.carrot2.text.preprocessing.pipeline.CompletePreprocessingPipelineDescriptor;
import org.carrot2.text.vsm.TermDocumentMatrixBuilderDescriptor;
import org.carrot2.util.attribute.AttributeValueSet;
import org.carrot2.util.attribute.AttributeValueSets;

/**
 * This example shows how to export a set of attribute values to XML. This code may come
 * in handy when you cannot <a href="http://download.carrot2.org/head/manual/#section.customizing.component-suites-and-attributes.saving-with-workbench">use 
 * Carrot2 Document Clustering Workbench to save the attributes XML</a>.
 * 
 * @see LoadingAttributeValuesFromXml
 */
public class SavingAttributeValuesToXml
{
    public static void main(String [] args) throws Exception
    {
        // The label passed to the constructor does not affect the webapp
        final AttributeValueSet attributeValueSet = new AttributeValueSet("Example set");

        // Add all the attributes you want to change to a non-default value.
        // You can use attribute keys from Workbench Attribute Info view or the manuals.
        Map<String, Object> attributes = new HashMap<String, Object>();

        LingoClusteringAlgorithmDescriptor
            .attributeBuilder(attributes)
            .desiredClusterCountBase(20)
            .matrixReducer()
                .factorizationQuality(FactorizationQuality.MEDIUM)
                .factorizationFactory(LocalNonnegativeMatrixFactorizationFactory.class);

        CompletePreprocessingPipelineDescriptor.attributeBuilder(attributes)
            .documentAssigner()
                .exactPhraseAssignment(true);

        TermDocumentMatrixBuilderDescriptor
            .attributeBuilder(attributes)
            .titleWordsBoost(2.5);

        attributeValueSet.setAttributeValues(attributes);
        
        // We'll need to wrap the exported attribute values in a AttributeValueSets,
        // even if we want to export just one set. 
        final AttributeValueSets attributeValueSets = new AttributeValueSets();
        attributeValueSets.addAttributeValueSet("example-id", attributeValueSet);

        attributeValueSets.serialize(System.out);
    }
}
