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

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.ReflectionUtils;

/**
 * An analyzer for the Chinese language, based on Lucene's Chinese analyzer.
 * A simple heuristic is employed to detect punctuation and simple numeric tokens.
 */
public final class ChineseAnalyzer extends Analyzer
{
    @Override
    public TokenStream tokenStream(String field, Reader reader)
    {
        try
        {
            // As other frameworks embedding Carrot2 (Solr, Nutch) may not distribute the
            // Smart Chinese Analyzer JAR by default due to its size, we need to make
            // this dependency optional too.
            final Class<?> tokenFilterClass = ReflectionUtils
                .classForName("org.apache.lucene.analysis.cn.smart.WordTokenFilter");
            final Class<?> sentenceTokenizerClass = ReflectionUtils
                .classForName("org.apache.lucene.analysis.cn.smart.SentenceTokenizer");

            // WordTokenFilter uses a shared dictionary, so creating multiple
            // instances of it should not be problem here.
            final Object sentenceTokenizer = sentenceTokenizerClass.getConstructor(
                Reader.class).newInstance(reader);
            final Object tokenFilter = tokenFilterClass.getConstructor(TokenStream.class)
                .newInstance(sentenceTokenizer);
            return new TokenTypePayloadSetter((TokenStream) tokenFilter);
        }
        catch (Exception e)
        {
            Logger
                .getLogger(ChineseAnalyzer.class)
                .warn(
                    "Could not instantiate Smart Chinese Analyzer, clustering quality "
                        + "of Chinese content may be degraded. For best quality clusters, "
                        + "make sure Lucene's Smart Chinese Analyzer JAR is in the classpath",
                    e);
            return new ExtendedWhitespaceTokenizer(reader);
        }

    }

    /**
     * Sets the token types required by Carrot2 based on the output from ChineseAnalyzer.
     */
    private static class TokenTypePayloadSetter extends TokenStream
    {
        private final TokenTypePayload payload = new TokenTypePayload();
        private final PayloadAttribute payloadAttribute;
        private final TermAttribute termAttribute;
        private final Pattern numeric = Pattern
            .compile("[\\-+'$]?\\d+([:\\-/,.]?\\d+)*[%$]?");
        private final TokenStream wrapped;
        private final MutableCharArray tempCharSequence;

        TokenTypePayloadSetter(TokenStream wrapped)
        {
            this.wrapped = wrapped;
            this.tempCharSequence = new MutableCharArray(new char [0]);
            this.payloadAttribute = (PayloadAttribute) addAttribute(PayloadAttribute.class);
            this.termAttribute = (TermAttribute) addAttribute(TermAttribute.class);
        }

        @Override
        public boolean incrementToken() throws IOException
        {
            final boolean hasNextToken = wrapped.incrementToken();
            if (hasNextToken)
            {
                final TermAttribute term = (TermAttribute) wrapped
                    .getAttribute(TermAttribute.class);

                // Looking at AttributeSource implementation, it's safer to
                // create a copy of the term attribute rather than override
                // getAttribute() and delegate to the wrapper there.
                termAttribute.setTermBuffer(term.termBuffer(), 0, term.termLength());
                termAttribute.setTermLength(term.termLength());

                final char [] image = term.termBuffer();
                tempCharSequence.reset(image, 0, term.termLength());
                if (tempCharSequence.length() == 1 && tempCharSequence.charAt(0) == ',')
                {
                    // ChineseAnalyzer seems to convert all punctuation to ',' characters
                    payload.setRawFlags(ITokenType.TT_PUNCTUATION);
                }
                else if (numeric.matcher(tempCharSequence).matches())
                {
                    payload.setRawFlags(ITokenType.TT_NUMERIC);
                }
                else
                {
                    payload.setRawFlags(ITokenType.TT_TERM);
                }
                payloadAttribute.setPayload(payload);
            }
            return hasNextToken;
        }

        @Override
        public boolean equals(Object other)
        {
            if (other instanceof TokenTypePayloadSetter)
            {
                return super.equals(other)
                    && ObjectUtils.equals(this.wrapped,
                        ((TokenTypePayloadSetter) other).wrapped);
            }
            else
            {
                return false;
            }
        }

        @Override
        public int hashCode()
        {
            return super.hashCode() ^ ObjectUtils.hashCode(this.wrapped);
        }
    }
}
