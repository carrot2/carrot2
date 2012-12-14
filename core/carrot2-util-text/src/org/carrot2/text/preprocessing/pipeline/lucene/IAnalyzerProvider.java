package org.carrot2.text.preprocessing.pipeline.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.linguistic.ILexicalDataFactory;

/**
 * @see LucenePreprocessingPipeline#analyzerProvider
 */
public interface IAnalyzerProvider
{
    Analyzer getAnalyzerFor(LanguageCode language, ILexicalDataFactory lexicalDataFactory);
}
