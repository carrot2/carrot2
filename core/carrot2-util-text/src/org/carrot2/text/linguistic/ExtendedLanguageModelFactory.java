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

package org.carrot2.text.linguistic;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cn.smart.SentenceTokenizer;
import org.apache.lucene.analysis.cn.smart.WordTokenFilter;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.analysis.ExtendedWhitespaceTokenizer;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.ReflectionUtils;
import org.carrot2.util.attribute.Bindable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link ILanguageModelFactory} that adds support for Chinese and Arabic. This class
 * is the only one within the core that has dependencies on Lucene APIs. I will be also
 * copied to Lucene/Solr repository, so that the developers can refactor Carrot2's
 * usage of Lucene APIs if needed.
 */
@Bindable(prefix = "DefaultLanguageModelFactory")
public class ExtendedLanguageModelFactory extends DefaultLanguageModelFactory
{
    /**
     * Provide an {@link IStemmer} implementation for a given language.
     */
    protected IStemmer createStemmer(LanguageCode language)
    {
        switch (language)
        {
            case CHINESE_SIMPLIFIED:
                /*
                 * Return identity stemmer for Chinese. Chinese requires proper prior word
                 * segmentation (from Lucene).
                 */
                return IdentityStemmer.INSTANCE;

            case ARABIC:
                /*
                 * We return specialized stemmer for Arabic (from Lucene).
                 */
                return ArabicStemmerFactory.createStemmer();

            default:
                return super.createStemmer(language);
        }
    }

    protected ITokenizer createTokenizer(LanguageCode language)
    {
        switch (language)
        {
            case CHINESE_SIMPLIFIED:
                return ChineseTokenizerFactory.createTokenizer();

                /*
                 * We use our own analyzer for Arabic. Lucene's version has special
                 * support for Nonspacing-Mark characters (see
                 * http://www.fileformat.info/info/unicode/category/Mn/index.htm), but we
                 * have them included as letters in the parser.
                 */
            case ARABIC:
                // Intentional fall-through.

            default:
                return super.createTokenizer(language);
        }
    }

    /**
     * Factory of {@link IStemmer} implementations for the {@link LanguageCode#ARABIC}
     * language. Requires <code>lucene-contrib</code> to be present in classpath,
     * otherwise an empty (identity) stemmer is returned.
     */
    private static class ArabicStemmerFactory
    {
        private final static Logger logger = org.slf4j.LoggerFactory
            .getLogger(ArabicStemmerFactory.class);

        private final static IStemmer stemmer;
        static
        {
            stemmer = createStemmerInternal();
            if (stemmer instanceof IdentityStemmer)
            {
                logger.warn("lucene-contrib classes not available in classpath.");
            }
        }

        /**
         * Adapter to lucene-contrib Arabic analyzers.
         */
        private static class LuceneStemmerAdapter implements IStemmer
        {
            private final org.apache.lucene.analysis.ar.ArabicStemmer delegate;
            private final org.apache.lucene.analysis.ar.ArabicNormalizer normalizer;

            private char [] buffer = new char [0];

            private LuceneStemmerAdapter() throws Exception
            {
                delegate = new org.apache.lucene.analysis.ar.ArabicStemmer();
                normalizer = new org.apache.lucene.analysis.ar.ArabicNormalizer();
            }

            public CharSequence stem(CharSequence word)
            {
                if (word.length() > buffer.length)
                {
                    buffer = new char [word.length()];
                }

                for (int i = 0; i < word.length(); i++)
                {
                    buffer[i] = word.charAt(i);
                }

                int newLen = normalizer.normalize(buffer, word.length());
                newLen = delegate.stem(buffer, newLen);

                if (newLen != word.length() || !equals(buffer, newLen, word))
                {
                    return CharBuffer.wrap(buffer, 0, newLen);
                }

                // Same-same.
                return null;
            }

            private boolean equals(char [] buffer, int len, CharSequence word)
            {
                assert len == word.length();

                for (int i = 0; i < len; i++)
                {
                    if (buffer[i] != word.charAt(i)) return false;
                }

                return true;
            }
        }

        /*
         * 
         */
        public static IStemmer createStemmer()
        {
            return stemmer;
        }

        /**
         * Attempts to instantiate <code>lucene-contrib</code> Arabic stemmer.
         */
        private static IStemmer createStemmerInternal()
        {
            try
            {
                return new LuceneStemmerAdapter();
            }
            catch (Throwable e)
            {
                return IdentityStemmer.INSTANCE;
            }
        }
    }

    static final class ChineseTokenizerFactory
    {
        static
        {
            try
            {
                ReflectionUtils.classForName(ChineseTokenizer.class.getName());
            }
            catch (ClassNotFoundException e)
            {
                LoggerFactory
                    .getLogger(ChineseTokenizer.class)
                    .warn(
                        "Could not instantiate Smart Chinese Analyzer, clustering quality "
                            + "of Chinese content may be degraded. For best quality clusters, "
                            + "make sure Lucene's Smart Chinese Analyzer JAR is in the classpath",
                        e);
            }
        }

        static ITokenizer createTokenizer()
        {
            try
            {
                System.out.println("Creating ChineseTokenizer");
                return new ChineseTokenizer();
            }
            catch (Throwable e)
            {
                System.out.println("No ChineseTokenizer");
                return new ExtendedWhitespaceTokenizer();
            }
        }

        private final static class ChineseTokenizer implements ITokenizer
        {
            private final static Pattern numeric = Pattern
                .compile("[\\-+'$]?\\d+([:\\-/,.]?\\d+)*[%$]?");

            private Tokenizer sentenceTokenizer;
            private TokenStream wordTokenFilter;
            private TermAttribute term = null;

            private final MutableCharArray tempCharSequence;

            private ChineseTokenizer()
            {
                this.tempCharSequence = new MutableCharArray(new char [0]);
                this.sentenceTokenizer = new SentenceTokenizer(null);
            }

            @Override
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

            @Override
            public void setTermBuffer(MutableCharArray array)
            {
                array.reset(term.termBuffer(), 0, term.termLength());
            }

            @Override
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
    }
}
