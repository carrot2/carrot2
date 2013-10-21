package org.carrot2.text.linguistic.lucene;

import org.apache.mahout.math.Arrays;
import org.carrot2.text.linguistic.IStemmer;
import org.carrot2.text.util.MutableCharArray;

/**
 * An adapter class applying Lucene's HindiAnalyzer's pipeline to the stream of
 * Carrot2 tokens.
 */
public class HindiStemmerAdapter implements IStemmer
{
    final IndicNormalizer indicNormalizer = new IndicNormalizer();
    final HindiNormalizer hindiNormalizer = new HindiNormalizer();
    final HindiStemmer hindiStemmer = new HindiStemmer();

    private char [] buffer = new char [0];

    @Override
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

        // Apply IndicNormalizationFilter.
        int len = word.length();
        len = indicNormalizer.normalize(buffer, len);
        len = hindiNormalizer.normalize(buffer, len);
        len = hindiStemmer.stem(buffer, len);

        if (!equals(word, buffer, len))
        {
            return new MutableCharArray(Arrays.copyOf(buffer, len));
        }
        else
        {
            return word;
        }
    }

    private boolean equals(CharSequence word, char [] text, int len)
    {
        if (word.length() != len)
        {
            return false;
        }

        for (int i = 0; i < len; i++)
        {
            if (word.charAt(i) != text[i])
            {
                return false;
            }
        }

        return true;
    }
}
