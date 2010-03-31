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

package org.carrot2.text.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.ReflectionUtils;
import org.slf4j.LoggerFactory;

/**
 * An analyzer for the Chinese language, based on Lucene's Smart Chinese analyzer. A
 * simple heuristic is employed to detect punctuation and simple numeric tokens.
 */
public final class ChineseTokenizer extends Tokenizer
{
    private final static Pattern numeric = Pattern
        .compile("[\\-+'$]?\\d+([:\\-/,.]?\\d+)*[%$]?");

    private final ITokenTypeAttribute type;
    private final TermAttribute term;
    private TermAttribute delegateTerm;

    private TokenStream delegate;
    private Tokenizer sentenceTokenizer;
    private final Class<?> tokenFilterClass;

    private final MutableCharArray tempCharSequence;

    public ChineseTokenizer()
    {
        super.addAttributeImpl(new TokenTypeAttributeImpl());
        this.type = addAttribute(ITokenTypeAttribute.class);
        this.term = addAttribute(TermAttribute.class);

        this.tempCharSequence = new MutableCharArray(new char [0]);

        try
        {
            /*
             * As other frameworks embedding Carrot2 (Solr, Nutch) may not distribute the
             * Smart Chinese Analyzer JAR by default due to its size, we need to make this
             * dependency optional too.
             */
            final Class<?> tokenizerClass = ReflectionUtils
                .classForName("org.apache.lucene.analysis.cn.smart.SentenceTokenizer");

            sentenceTokenizer = (Tokenizer) tokenizerClass.getConstructor(Reader.class)
                .newInstance((Reader) null);

            tokenFilterClass = ReflectionUtils
                .classForName("org.apache.lucene.analysis.cn.smart.WordTokenFilter");
        }
        catch (Exception e)
        {
            LoggerFactory
                .getLogger(ChineseTokenizer.class)
                .warn(
                    "Could not instantiate Smart Chinese Analyzer, clustering quality "
                        + "of Chinese content may be degraded. For best quality clusters, "
                        + "make sure Lucene's Smart Chinese Analyzer JAR is in the classpath",
                    e);

            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
    }

    @Override
    public boolean incrementToken() throws IOException
    {
        clearAttributes();

        final boolean hasNextToken = delegate.incrementToken();
        if (hasNextToken)
        {
            /*
             * Attributes are singletons. Since we're delegating to another TokenStream
             * (which has its own copy of each attribute's instance), we must copy data
             * over. Shouldn't be much of a problem.
             */
            final char [] image = delegateTerm.termBuffer();
            final int termLength = delegateTerm.termLength();
            term.setTermBuffer(image, 0, termLength);
            term.setTermLength(termLength);

            short flags = 0;
            tempCharSequence.reset(image, 0, termLength);
            if (termLength == 1 && image[0] == ',')
            {
                // ChineseTokenizer seems to convert all punctuation to ',' characters
                flags = ITokenTypeAttribute.TT_PUNCTUATION;
            }
            else if (numeric.matcher(tempCharSequence).matches())
            {
                flags = ITokenTypeAttribute.TT_NUMERIC;
            }
            else
            {
                flags = ITokenTypeAttribute.TT_TERM;
            }
            type.setRawFlags(flags);
        }

        return hasNextToken;
    }

    @Override
    public void close() throws IOException
    {
        super.close();
        delegate.close();
    }

    @Override
    public void reset(Reader input) throws IOException
    {
        super.reset(input);

        // WordTokenFilter does not have a reset method, we need to create a new one
        try
        {
            sentenceTokenizer.reset(input);

            delegate = (TokenStream) tokenFilterClass.getConstructor(TokenStream.class)
                .newInstance(sentenceTokenizer);
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }

        this.delegateTerm = delegate.getAttribute(TermAttribute.class);
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof ChineseTokenizer)
        {
            return super.equals(other);
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        // Just to document we're fine with using AttributeSource.hashCode()
        return super.hashCode();
    }
}
