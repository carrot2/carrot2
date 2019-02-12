
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

import java.util.HashMap;
import java.util.Map;

import org.carrot2.core.attribute.Init;
import org.carrot2.text.linguistic.ILexicalDataFactory;
import org.carrot2.text.linguistic.IStemmerFactory;
import org.carrot2.text.preprocessing.PreprocessingComponentTestBase;
import org.carrot2.text.preprocessing.TestLexicalDataFactory;
import org.carrot2.text.preprocessing.TestStemmer;
import org.carrot2.text.preprocessing.pipeline.CompletePreprocessingPipeline;
import org.carrot2.util.attribute.AttributeBinder;
import org.carrot2.util.attribute.Input;
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

        languageModel.stemmer = createStemmerFactory().getStemmer(super.languageModel.language);
        languageModel.tokenizer = createTokenizerFactory().getTokenizer(super.languageModel.language);
        languageModel.lexicalData = createLexicalDataFactory().getLexicalData(super.languageModel.language);
        
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
    protected IStemmerFactory createStemmerFactory()
    {
        return (lang) -> new TestStemmer();
    }

    @Override
    protected ILexicalDataFactory createLexicalDataFactory()
    {
        return new TestLexicalDataFactory();
    }
}
