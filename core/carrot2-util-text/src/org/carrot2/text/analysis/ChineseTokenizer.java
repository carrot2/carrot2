
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
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
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
    private final TokenTypePayload payload = new TokenTypePayload();
    private final PayloadAttribute payloadAttribute;
    private final TermAttribute termAttribute;
    private final Pattern numeric = Pattern
        .compile("[\\-+'$]?\\d+([:\\-/,.]?\\d+)*[%$]?");
    private TokenStream wrapped;
    private final MutableCharArray tempCharSequence;

    private boolean chineseTokenizerAvailable;
    private Object sentenceTokenizer;

    public ChineseTokenizer()
    {
        this.payloadAttribute = addAttribute(PayloadAttribute.class);
        this.termAttribute = addAttribute(TermAttribute.class);
        this.tempCharSequence = new MutableCharArray(new char [0]);

        try
        {
            /*
             * As other frameworks embedding Carrot2 (Solr, Nutch) may not distribute the
             * Smart Chinese Analyzer JAR by default due to its size, we need to make this
             * dependency optional too.
             */
            final Class<?> sentenceTokenizerClass = ReflectionUtils
                .classForName("org.apache.lucene.analysis.cn.smart.SentenceTokenizer");

            sentenceTokenizer = sentenceTokenizerClass.getConstructor(Reader.class)
                .newInstance((Reader) null);
            chineseTokenizerAvailable = true;
        }
        catch (Exception e)
        {
            chineseTokenizerAvailable = false;
            logWarning(e);
            wrapped = new ExtendedWhitespaceTokenizer();
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
    }

    @Override
    public boolean incrementToken() throws IOException
    {
        final boolean hasNextToken = wrapped.incrementToken();
        if (hasNextToken)
        {
            final TermAttribute term = wrapped.getAttribute(TermAttribute.class);

            // Looking at AttributeSource implementation, it's safer to
            // create a copy of the term attribute rather than override
            // getAttribute() and delegate to the wrapper there.
            termAttribute.setTermBuffer(term.termBuffer(), 0, term.termLength());
            termAttribute.setTermLength(term.termLength());

            final char [] image = term.termBuffer();
            tempCharSequence.reset(image, 0, term.termLength());
            if (tempCharSequence.length() == 1 && tempCharSequence.charAt(0) == ',')
            {
                // ChineseTokenizer seems to convert all punctuation to ',' characters
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
    public void close() throws IOException
    {
        super.close();
        wrapped.close();
    }

    @Override
    public void reset(Reader input) throws IOException
    {
        super.reset(input);
        if (chineseTokenizerAvailable)
        {
            // WordTokenFilter does not have a reset method, we need to create a new one
            try
            {
                ((Tokenizer)sentenceTokenizer).reset(input);
                final Class<?> tokenFilterClass = ReflectionUtils
                    .classForName("org.apache.lucene.analysis.cn.smart.WordTokenFilter");
                wrapped = (TokenStream) tokenFilterClass.getConstructor(TokenStream.class)
                    .newInstance(sentenceTokenizer);
            }
            catch (Exception e)
            {
                logWarning(e);
                throw ExceptionUtils.wrapAsRuntimeException(e);
            }
        }
        else
        {
            ((ExtendedWhitespaceTokenizer)wrapped).reset(input);
        }
    }

    private void logWarning(Exception e)
    {
        LoggerFactory
            .getLogger(ChineseTokenizer.class)
            .warn(
                "Could not instantiate Smart Chinese Analyzer, clustering quality "
                    + "of Chinese content may be degraded. For best quality clusters, "
                    + "make sure Lucene's Smart Chinese Analyzer JAR is in the classpath",
                e);
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
