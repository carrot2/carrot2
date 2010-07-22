
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
import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ar.ArabicNormalizer;
import org.apache.lucene.analysis.ar.ArabicStemmer;
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
import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.DanishStemmer;
import org.tartarus.snowball.ext.DutchStemmer;
import org.tartarus.snowball.ext.EnglishStemmer;
import org.tartarus.snowball.ext.FinnishStemmer;
import org.tartarus.snowball.ext.FrenchStemmer;
import org.tartarus.snowball.ext.GermanStemmer;
import org.tartarus.snowball.ext.HungarianStemmer;
import org.tartarus.snowball.ext.ItalianStemmer;
import org.tartarus.snowball.ext.NorwegianStemmer;
import org.tartarus.snowball.ext.PortugueseStemmer;
import org.tartarus.snowball.ext.RomanianStemmer;
import org.tartarus.snowball.ext.RussianStemmer;
import org.tartarus.snowball.ext.SpanishStemmer;
import org.tartarus.snowball.ext.SwedishStemmer;
import org.tartarus.snowball.ext.TurkishStemmer;

/**
 * A factory of {@link ILanguageModel} objects. Internally, for a number of languages,
 * this class creates adapters from Lucene's stemmers and tokenizers to Carrot2-specific
 * interfaces. This is the only class in Carrot2 core that depends on Lucene APIs. 
 * 
 * @see LanguageCode
 */
@Bindable(prefix = "DefaultLanguageModelFactory")
public class DefaultLanguageModelFactory extends BaseLanguageModelFactory
{
    final static Logger logger = org.slf4j.LoggerFactory
        .getLogger(DefaultLanguageModelFactory.class);

    /**
     * Provide an {@link IStemmer} implementation for a given language.
     */
    protected IStemmer createStemmer(LanguageCode language)
    {
        switch (language)
        {
            case POLISH:
                /*
                 * For Polish, we use a dictionary-backed stemmer
                 * from the Morfologik project.
                 */
                return PolishStemmerFactory.createStemmer();

            case ARABIC:
                return ArabicStemmerFactory.createStemmer(); 
                
            case CHINESE_SIMPLIFIED:
                // No stemming in Chinese, returning identity stemmer to avoid a useless warning.
                return IdentityStemmer.INSTANCE;
                
            default:
                /*
                 * For other languages, try to use snowball's stemming. 
                 */
                return SnowballStemmerFactory.createStemmer(language);
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
                return new ExtendedWhitespaceTokenizer();
        }
    }

    /**
     * Factory of {@link IStemmer} implementations from the <code>snowball</code> project.
     */
    private final static class SnowballStemmerFactory
    {
        /**
         * Static hard mapping from language codes to stemmer classes in Snowball. This
         * mapping is not dynamic because we want to keep the possibility to obfuscate these
         * classes.
         */
        private static HashMap<LanguageCode, Class<? extends SnowballProgram>> snowballStemmerClasses;
        static
        {
            snowballStemmerClasses = new HashMap<LanguageCode, Class<? extends SnowballProgram>>();
            snowballStemmerClasses.put(LanguageCode.DANISH, DanishStemmer.class);
            snowballStemmerClasses.put(LanguageCode.DUTCH, DutchStemmer.class);
            snowballStemmerClasses.put(LanguageCode.ENGLISH, EnglishStemmer.class);
            snowballStemmerClasses.put(LanguageCode.FINNISH, FinnishStemmer.class);
            snowballStemmerClasses.put(LanguageCode.FRENCH, FrenchStemmer.class);
            snowballStemmerClasses.put(LanguageCode.GERMAN, GermanStemmer.class);
            snowballStemmerClasses.put(LanguageCode.HUNGARIAN, HungarianStemmer.class);
            snowballStemmerClasses.put(LanguageCode.ITALIAN, ItalianStemmer.class);
            snowballStemmerClasses.put(LanguageCode.NORWEGIAN, NorwegianStemmer.class);
            snowballStemmerClasses.put(LanguageCode.PORTUGUESE, PortugueseStemmer.class);
            snowballStemmerClasses.put(LanguageCode.ROMANIAN, RomanianStemmer.class);
            snowballStemmerClasses.put(LanguageCode.RUSSIAN, RussianStemmer.class);
            snowballStemmerClasses.put(LanguageCode.SPANISH, SpanishStemmer.class);
            snowballStemmerClasses.put(LanguageCode.SWEDISH, SwedishStemmer.class);
            snowballStemmerClasses.put(LanguageCode.TURKISH, TurkishStemmer.class);
        }

        /**
         * An adapter converting Snowball programs into {@link IStemmer} interface.
         */
        private static class SnowballStemmerAdapter implements IStemmer
        {
            private final SnowballProgram snowballStemmer;

            public SnowballStemmerAdapter(SnowballProgram snowballStemmer)
            {
                this.snowballStemmer = snowballStemmer;
            }

            public CharSequence stem(CharSequence word)
            {
                snowballStemmer.setCurrent(word.toString());
                if (snowballStemmer.stem())
                {
                    return snowballStemmer.getCurrent();
                }
                else
                {
                    return null;
                }
            }
        }

        /**
         * Create and return an {@link IStemmer} adapter for a {@link SnowballProgram} for a
         * given language code. An identity stemmer is returned for unknown languages.
         */
        public static IStemmer createStemmer(LanguageCode language)
        {
            final Class<? extends SnowballProgram> stemmerClazz = snowballStemmerClasses
                .get(language);

            if (stemmerClazz == null)
            {
                logger.warn("No Snowball stemmer class for: " + language.name()
                    + ". Quality of clustering may be degraded.");
                return IdentityStemmer.INSTANCE;
            }

            try
            {
                return new SnowballStemmerAdapter(stemmerClazz.newInstance());
            }
            catch (Exception e)
            {
                logger.warn("Could not instantiate snowball stemmer" + " for language: "
                    + language.name() + ". Quality of clustering may be degraded.", e);

                return IdentityStemmer.INSTANCE;
            }
        }
    }

    /**
     * Factory of {@link IStemmer} implementations for the {@link LanguageCode#ARABIC}
     * language. Requires <code>lucene-contrib</code> to be present in classpath,
     * otherwise an empty (identity) stemmer is returned.
     */
    private static class ArabicStemmerFactory
    {
        static
        {
            try
            {
                ReflectionUtils.classForName(ArabicStemmer.class.getName(), false);
                ReflectionUtils.classForName(ArabicNormalizer.class.getName(), false);
            }
            catch (Throwable e)
            {
                logger
                    .warn(
                        "Could not instantiate Lucene stemmer for Arabic, clustering quality "
                            + "of Chinese content may be degraded. For best quality clusters, "
                            + "make sure Lucene's Arabic analyzer JAR is in the classpath");
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

    /**
     * Creates tokenizers that adapt Lucene's Smart Chinese Tokenizer to Carrot2's
     * {@link ITokenizer}.
     */
    private static final class ChineseTokenizerFactory
    {
        static
        {
            try
            {
                ReflectionUtils.classForName(WordTokenFilter.class.getName(), false);
                ReflectionUtils.classForName(SentenceTokenizer.class.getName(), false);
            }
            catch (Throwable e)
            {
                logger
                    .warn(
                        "Could not instantiate Smart Chinese Analyzer, clustering quality "
                            + "of Chinese content may be degraded. For best quality clusters, "
                            + "make sure Lucene's Smart Chinese Analyzer JAR is in the classpath");
            }
        }

        static ITokenizer createTokenizer()
        {
            try
            {
                return new ChineseTokenizer();
            }
            catch (Throwable e)
            {
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
    }
}
