
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

package org.carrot2.text.linguistic.lucene;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cn.smart.SentenceTokenizer;
import org.apache.lucene.analysis.cn.smart.WordTokenFilter;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.ITokenizerFactory;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.ExceptionUtils;

/**
 * Tokenizer factory for Simplified Chinese.
 * 
 * @see LanguageCode#CHINESE_SIMPLIFIED
 */
public class ChineseSimplifiedTokenizerFactory implements ITokenizerFactory
{
    /**
     * 
     */
    private final static class TokenizerAdapter implements ITokenizer
    {
        private final static Pattern numeric = Pattern
            .compile("[\\-+'$]?\\d+([:\\-/,.]?\\d+)*[%$]?");

        private Tokenizer sentenceTokenizer;
        private TokenStream wordTokenFilter;
        private TermAttribute term = null;

        private final MutableCharArray tempCharSequence;

        private TokenizerAdapter()
        {
            this.tempCharSequence = new MutableCharArray(new char [0]);
            this.sentenceTokenizer = new SentenceTokenizer(null);
        }

        public short nextToken() throws IOException
        {
            final boolean hasNextToken = wordTokenFilter.incrementToken();
            if (hasNextToken)
            {
                short flags = 0;
                final char [] image = term.termBuffer();
                final int length = term.termLength();
                tempCharSequence.reset(image, 0, length);
                if (length == 1 && image[0] == ',')
                {
                    // ChineseTokenizer seems to convert all punctuation to ','
                    // characters
                    flags = ITokenizer.TT_PUNCTUATION;
                }
                else if (numeric.matcher(tempCharSequence).matches())
                {
                    flags = ITokenizer.TT_NUMERIC;
                }
                else
                {
                    flags = ITokenizer.TT_TERM;
                }
                return flags;
            }

            return ITokenizer.TT_EOF;
        }

        public void setTermBuffer(MutableCharArray array)
        {
            array.reset(term.termBuffer(), 0, term.termLength());
        }

        public void reset(Reader input) throws IOException
        {
            try
            {
                sentenceTokenizer.reset(input);
                wordTokenFilter = new WordTokenFilter(sentenceTokenizer);
                this.term = wordTokenFilter.addAttribute(TermAttribute.class);
            }
            catch (Exception e)
            {
                throw ExceptionUtils.wrapAsRuntimeException(e);
            }
        }
    }

    @Override
    public ITokenizer createInstance()
    {
        return new TokenizerAdapter();
    }
}
