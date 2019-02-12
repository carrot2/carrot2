
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.vsm;

import org.carrot2.text.linguistic.*;
import org.carrot2.text.preprocessing.PreprocessingComponentTestBase;
import org.carrot2.text.preprocessing.TestLexicalData;
import org.carrot2.text.preprocessing.TestStemmer;
import org.carrot2.text.preprocessing.pipeline.CompletePreprocessingPipeline;
import org.junit.Before;

/**
 * A base class for tests requiring that the main term-document document matrix is built.
 */
public class TermDocumentMatrixBuilderTestBase extends PreprocessingComponentTestBase
{
    /** Matrix builder */
    protected TermDocumentMatrixBuilder matrixBuilder;

    /** VSM processing context with all the data */
    protected VectorSpaceModelContext vsmContext;

    /** Preprocessing pipeline used for tests */
    protected CompletePreprocessingPipeline preprocessingPipeline;
    
    @Before
    public void setUpMatrixBuilder() throws Exception
    {
        preprocessingPipeline = new CompletePreprocessingPipeline();
        preprocessingPipeline.labelFilterProcessor.minLengthLabelFilter.enabled = false;

        languageModel = new LanguageModel(createStemmer(), createTokenizer(), createLexicalData());

        matrixBuilder = new TermDocumentMatrixBuilder();
        matrixBuilder.termWeighting = new TfTermWeighting();
        matrixBuilder.maxWordDf = 1.0;
    }

    protected void buildTermDocumentMatrix()
    {
        context = preprocessingPipeline.preprocess(
            documents,
            query,
            languageModel);
        
        vsmContext = new VectorSpaceModelContext(context);
        matrixBuilder.buildTermDocumentMatrix(vsmContext);
        matrixBuilder.buildTermPhraseMatrix(vsmContext);
    }

    @Override
    protected IStemmer createStemmer()
    {
        return new TestStemmer();
    }

    @Override
    protected ILexicalData createLexicalData()
    {
        return new TestLexicalData();
    }
}
