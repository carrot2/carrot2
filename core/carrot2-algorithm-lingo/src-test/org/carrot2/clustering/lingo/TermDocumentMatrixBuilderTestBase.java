package org.carrot2.clustering.lingo;

import org.carrot2.text.linguistic.LanguageModelFactory;
import org.carrot2.text.preprocessing.*;
import org.junit.Before;

/**
 * A base class for tests requiring that the main term-document document matrix is built.
 */
public class TermDocumentMatrixBuilderTestBase extends PreprocessingComponentTestBase
{
    /** Matrix builder */
    protected TermDocumentMatrixBuilder matrixBuilder;

    /** Lingo processing context with all the data */
    protected LingoProcessingContext lingoContext;

    protected LabelFilterProcessor labelFilterProcessor;
    
    @Before
    public void setUpMatrixBuilder()
    {
        matrixBuilder = new TermDocumentMatrixBuilder();
        labelFilterProcessor = new LabelFilterProcessor();
        labelFilterProcessor.minLengthLabelFilter.enabled = false;
    }

    protected void buildTermDocumentMatrix()
    {
        Tokenizer tokenizer = new Tokenizer();
        CaseNormalizer caseNormalizer = new CaseNormalizer();
        LanguageModelStemmer languageModelStemmer = new LanguageModelStemmer();
        PhraseExtractor phraseExtractor = new PhraseExtractor();
        StopListMarker stopListMarker = new StopListMarker();
        DocumentAssigner documentAssigner = new DocumentAssigner();

        tokenizer.tokenize(context);
        caseNormalizer.normalize(context);
        languageModelStemmer.stem(context);
        phraseExtractor.extractPhrases(context);
        stopListMarker.mark(context);
        labelFilterProcessor.process(context);
        documentAssigner.assign(context);

        lingoContext = new LingoProcessingContext(context);
        matrixBuilder.build(lingoContext, new TfTermWeighting());
    }

    @Override
    protected LanguageModelFactory createLanguageModelFactory()
    {
        return new TestLanguageModelFactory();
    }
}