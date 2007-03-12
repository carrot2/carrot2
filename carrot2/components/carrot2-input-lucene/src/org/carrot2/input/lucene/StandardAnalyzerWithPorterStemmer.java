package org.carrot2.input.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * A standard analyzer with Porter stemmer
 * 
 * @author Stanislaw Osinski
 */
public class StandardAnalyzerWithPorterStemmer extends
    StandardAnalyzer
{
    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return new PorterStemFilter(super.tokenStream(fieldName,
            reader));
    }
}