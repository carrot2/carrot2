
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic.lucene;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.th.ThaiTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.ExceptionUtils;

/**
 * Thai tokenizer implemented using Lucene's {@link ThaiTokenizer}.
 */
public final class ThaiTokenizerAdapter implements ITokenizer
{
    private CharTermAttribute term = null;

    private final MutableCharArray tempCharSequence;
    private ThaiTokenizer tokenizer;

    public ThaiTokenizerAdapter()
    {
        this.tempCharSequence = new MutableCharArray(new char [0]);
        if (!platformSupportsThai()) {
            throw new RuntimeException("Thai segmentation not supported on this platform.");
        }
    }

    public short nextToken() throws IOException
    {
        final boolean hasNextToken = tokenizer.incrementToken();
        if (hasNextToken)
        {
            final char [] image = term.buffer();
            final int length = term.length();
            tempCharSequence.reset(image, 0, length);

            return ITokenizer.TT_TERM;
        }

        return ITokenizer.TT_EOF;
    }

    public void setTermBuffer(MutableCharArray array)
    {
        array.reset(term.buffer(), 0, term.length());
    }

    public void reset(Reader input) throws IOException
    {
        assert input != null;
        try
        {
            this.tokenizer = new ThaiTokenizer();
            tokenizer.setReader(input);

            this.term = tokenizer.addAttribute(CharTermAttribute.class);
            this.tokenizer.reset();
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
    }
    
    /**
     * Check support for Thai.
     */
    public static boolean platformSupportsThai()
    {
        try {
           return ThaiTokenizer.DBBI_AVAILABLE; 
        } catch (Throwable e) {
            return false;
        }
    }    
}
