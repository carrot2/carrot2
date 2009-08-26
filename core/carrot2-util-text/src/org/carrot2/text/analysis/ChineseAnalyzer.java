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
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.carrot2.text.util.MutableCharArray;

/**
 * An analyzer for the Chinese language, based on Lucene's
 * {@link org.apache.lucene.analysis.cn.ChineseAnalyzer}. A simple heuristic is employed
 * to detect punctuation and simple numeric tokens.
 */
public final class ChineseAnalyzer extends Analyzer
{
    @Override
    public TokenStream tokenStream(String field, Reader reader)
    {
        // WordTokenFilter uses a shared dictionary, so creating multiple
        // instances of it should not be problem here.
        return new TokenTypePayloadSetter(new WordTokenFilter(new SentenceTokenizer(
            reader)));
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

    }
}
