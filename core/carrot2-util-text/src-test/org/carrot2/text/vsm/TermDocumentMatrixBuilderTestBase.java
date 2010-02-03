
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

package org.carrot2.text.vsm;

import org.carrot2.text.linguistic.ILanguageModelFactory;
import org.carrot2.text.preprocessing.*;
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
    public void setUpMatrixBuilder()
    {
        preprocessingPipeline = new CompletePreprocessingPipeline();
        preprocessingPipeline.labelFilterProcessor.minLengthLabelFilter.enabled = false;
        matrixBuilder = new TermDocumentMatrixBuilder();
        matrixBuilder.termWeighting = new TfTermWeighting();
    }

    protected void buildTermDocumentMatrix()
    {
        preprocessingPipeline.preprocess(context);
        
        vsmContext = new VectorSpaceModelContext(context);
        matrixBuilder.buildTermDocumentMatrix(vsmContext);
        matrixBuilder.buildTermPhraseMatrix(vsmContext);
    }

    @Override
    protected ILanguageModelFactory createLanguageModelFactory()
    {
        return new TestLanguageModelFactory();
    }
}
