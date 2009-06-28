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

package org.carrot2.text.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.cn.smart.*;

/**
 * An {@link Analyzer} instance tokenizing using {@link ExtendedWhitespaceTokenizer}.
 */
public final class ChineseAnalyzer extends Analyzer
{
    private WordSegmenter wordSegmenter = new WordSegmenter();

    /*
     * 
     */
    public TokenStream reusableTokenStream(String field, final Reader reader)
    {
        /*
         * Avoid using ThreadLocal in Analyzer so that the context class loader's
         * reference is not stored in the thread.
         * http://issues.carrot2.org/browse/CARROT-414
         */
        return tokenStream(field, reader);
    }

    /*
     * 
     */
    @Override
    public TokenStream tokenStream(String field, Reader reader)
    {
        return new TokenTypePayloadSetter(new WordTokenizer(
            new SentenceTokenizer(reader), wordSegmenter));
    }

    private static class TokenTypePayloadSetter extends TokenStream
    {
        private final TokenTypePayload tokenPayload = new TokenTypePayload();

        private TokenStream wrapped;

        TokenTypePayloadSetter(TokenStream wrapped)
        {
            this.wrapped = wrapped;
        }

        @Override
        public Token next(Token reusableToken) throws IOException
        {
            final Token token = wrapped.next(reusableToken);
            if (token != null)
            {
                tokenPayload.setRawFlags(ITokenType.TT_TERM);
                token.setPayload(tokenPayload);
            }
            return token;
        }
    }
}
