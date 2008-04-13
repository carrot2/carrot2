package org.carrot2.text.preprocessing;

import org.carrot2.text.CharSequenceIntMap;
import org.carrot2.text.linguistic.LanguageModel;

/**
 * Stemming contract for {@link Preprocessor}. 
 * 
 * @see PreprocessingTasks#STEMMING
 */
public interface StemmingTask
{
    /**
     * Creates stems of inflected word forms and remaps token sequence from the given
     * context to stemmed token indices.
     * <p>
     * If case normalization is applied, then stemming operates on case-normalized tokens.
     * Otherwise raw tokens are used.
     */
    public abstract void stem(CharSequenceIntMap tokenCoder,
        PreprocessingContext context, LanguageModel language);

    /**
     * @return Returns remapped sequence of tokens.
     */
    public abstract int [] getTokensStemmed();
}