package org.carrot2.text.linguistic;

import org.tartarus.snowball.SnowballProgram;

public class SnowballStemmerFactory implements IStemmerFactory
{
    private Class<? extends SnowballProgram> clazz;

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

    public SnowballStemmerFactory(Class<? extends SnowballProgram> clazz)
    {
        this.clazz = clazz;
    }

    @Override
    public IStemmer createInstance()
    {
        try
        {
            return new SnowballStemmerAdapter(clazz.newInstance());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
