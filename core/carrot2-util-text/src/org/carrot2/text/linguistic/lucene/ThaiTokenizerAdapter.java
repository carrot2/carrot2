/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic.lucene;

import java.io.IOException;
import java.io.Reader;
import java.text.BreakIterator;
import java.util.Locale;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.th.ThaiWordFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Version;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.ExceptionUtils;

/**
 * Thai tokenizer implemented using Lucene's {@link ThaiWordFilter}.
 */
public final class ThaiTokenizerAdapter implements ITokenizer
{
    private TokenStream wordTokenFilter;
    private CharTermAttribute term = null;
    private TypeAttribute type = null;

    private final MutableCharArray tempCharSequence;

    public ThaiTokenizerAdapter()
    {
        this.tempCharSequence = new MutableCharArray(new char [0]);
        if (!platformSupportsThai()) {
            throw new RuntimeException("Thai segmentation not supported on this platform.");
        }
    }

    public short nextToken() throws IOException
    {
        final boolean hasNextToken = wordTokenFilter.incrementToken();
        if (hasNextToken)
        {
            final char [] image = term.buffer();
            final int length = term.length();
            tempCharSequence.reset(image, 0, length);

            short flags = 0;
            final String typeString = type.type();
            if (typeString.equals("<SOUTHEAST_ASIAN>") || typeString.equals("<ALPHANUM>"))
            {
                flags = ITokenizer.TT_TERM;
            }
            else if (typeString.equals("<NUM>"))
            {
                flags = ITokenizer.TT_NUMERIC;
            }
            else
            {
                flags = ITokenizer.TT_PUNCTUATION;
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
            this.wordTokenFilter = new ThaiWordFilter(Version.LUCENE_31,
                new StandardTokenizer(Version.LUCENE_31, input));
            this.term = wordTokenFilter.addAttribute(CharTermAttribute.class);
            this.type = wordTokenFilter.addAttribute(TypeAttribute.class);
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
            // Check if Thai break iteration is supported, code taken from Lucene's ThaiWordFilter. 
            final BreakIterator proto = BreakIterator.getWordInstance(new Locale("th"));
            proto.setText("ภาษาไทย");
            return proto.isBoundary(4);
        } catch (Throwable e) {
            return false;
        }
    }    
}
