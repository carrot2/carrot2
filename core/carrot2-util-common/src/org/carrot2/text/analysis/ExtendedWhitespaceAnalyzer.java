package org.carrot2.text.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.carrot2.util.ExceptionUtils;

/**
 * An {@link Analyzer} instance tokenizing using {@link ExtendedWhitespaceTokenizer}.
 */
public final class ExtendedWhitespaceAnalyzer extends Analyzer
{
    /*
     * 
     */
    public TokenStream reusableTokenStream(String field, final Reader reader)
    {
        Tokenizer tokenizer = (Tokenizer) getPreviousTokenStream();
        if (tokenizer == null)
        {
            tokenizer = (Tokenizer) tokenStream(field, reader);
            setPreviousTokenStream(tokenizer);
        }
        else
        {
            try
            {
                tokenizer.reset(reader);
            }
            catch (IOException e)
            {
                throw ExceptionUtils.wrapAs(RuntimeException.class, e);
            }
        }
        return tokenizer;
    }

    /*
     * 
     */
    @Override
    public TokenStream tokenStream(String field, Reader reader)
    {
        return new ExtendedWhitespaceTokenizer(reader);
    }
}
