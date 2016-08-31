package org.carrot2.text.linguistic;

import java.util.Arrays;

import org.carrot2.text.linguistic.snowball.SnowballProgram;
import org.carrot2.text.util.MutableCharArray;

/**
 * An adapter converting Snowball programs into {@link IStemmer} interface.
 */
class SnowballStemmerAdapter implements IStemmer
{
    private final SnowballProgram s;

    public SnowballStemmerAdapter(SnowballProgram s)
    {
        this.s = s;
    }

    public CharSequence stem(CharSequence word)
    {
        final int len = word.length();
        char [] buffer = s.getCurrentBuffer();
        if (buffer.length < len)
            buffer = new char [len];

        for (int i = word.length(); --i >= 0;)
            buffer[i] = word.charAt(i);
        s.setCurrent(buffer, len);

        if (s.stem())
        {
            return new MutableCharArray(Arrays.copyOf(
                s.getCurrentBuffer(), 
                s.getCurrentBufferLength()));
        }
        else
        {
            return null;
        }
    }
}