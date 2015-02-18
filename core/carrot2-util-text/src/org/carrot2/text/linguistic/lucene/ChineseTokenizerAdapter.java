
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2015, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic.lucene;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cn.smart.SentenceTokenizer;
import org.apache.lucene.analysis.cn.smart.WordTokenFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.ExceptionUtils;

/**
 * 
 */
public final class ChineseTokenizerAdapter implements ITokenizer
{
    private final static Pattern numeric = Pattern
        .compile("[\\-+'$]?\\d+([:\\-/,.]?\\d+)*[%$]?");

    private Tokenizer sentenceTokenizer;
    private TokenStream wordTokenFilter;
    private CharTermAttribute term = null;

    private final MutableCharArray tempCharSequence;

    public ChineseTokenizerAdapter()
    {
        this.tempCharSequence = new MutableCharArray(new char [0]);
        this.sentenceTokenizer = new SentenceTokenizer(new StringReader(""));
    }

    public short nextToken() throws IOException
    {
        final boolean hasNextToken = wordTokenFilter.incrementToken();
        if (hasNextToken)
        {
            short flags = 0;
            final char [] image = term.buffer();
            final int length = term.length();
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
        array.reset(term.buffer(), 0, term.length());
    }

    public void reset(Reader input) throws IOException
    {
        try
        {
            if (wordTokenFilter != null) {
                wordTokenFilter.end();
                wordTokenFilter.close();
            }

            sentenceTokenizer.setReader(input);
            wordTokenFilter = new WordTokenFilter(sentenceTokenizer);
            this.term = wordTokenFilter.addAttribute(CharTermAttribute.class);
            wordTokenFilter.reset();
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
    }
}
