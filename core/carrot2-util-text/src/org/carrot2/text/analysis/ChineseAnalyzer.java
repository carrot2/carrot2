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
import java.util.regex.Pattern;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.cn.smart.*;
import org.carrot2.text.util.MutableCharArray;

/**
 * An analyzer for the Chinese language, based on Lucene's
 * {@link org.apache.lucene.analysis.cn.ChineseAnalyzer}. A simple heuristic is employed
 * to detect punctuation and simple numeric tokens.
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

    /**
     * Sets the token types required by Carrot2 based on the output from ChineseAnalyzer.
     */
    private static class TokenTypePayloadSetter extends TokenStream
    {
        private final TokenTypePayload tokenPayload = new TokenTypePayload();
        private final Pattern numeric = Pattern
            .compile("[\\-+'$]?\\d+([:\\-/,.]?\\d+)*[%$]?");
        private TokenStream wrapped;
        private MutableCharArray tempCharSequence;

        TokenTypePayloadSetter(TokenStream wrapped)
        {
            this.wrapped = wrapped;
            this.tempCharSequence = new MutableCharArray(new char [0]);
        }

        @Override
        @SuppressWarnings("deprecation")
        public Token next(Token reusableToken) throws IOException
        {
            final Token token = wrapped.next(reusableToken);
            if (token != null)
            {
                final char [] image = token.termBuffer();
                tempCharSequence.reset(image, 0, token.termLength());
                if (tempCharSequence.length() == 1 && tempCharSequence.charAt(0) == ',')
                {
                    // ChineseAnalyzer seems to convert all punctuation to ',' characters
                    tokenPayload.setRawFlags(ITokenType.TT_PUNCTUATION);
                }
                else if (numeric.matcher(tempCharSequence).matches())
                {
                    tokenPayload.setRawFlags(ITokenType.TT_NUMERIC);
                }
                else
                {
                    tokenPayload.setRawFlags(ITokenType.TT_TERM);
                }
                token.setPayload(tokenPayload);
            }
            return token;
        }
    }
}
