package org.carrot2.text.preprocessing;

import org.carrot2.text.CharSequenceIntMap;
import org.carrot2.text.linguistic.LanguageModelFactory;

/**
 * Case normalization contract for {@link Preprocessor}.
 * 
 * @see PreprocessingTasks#CASE_NORMALIZE
 */
public interface CaseNormalizerTask
{
    /**
     * Normalize token images in <code>allTokenImages</code> and remap token indices in
     * <code>allTokens</code> using new token codes.
     */
    public abstract void normalize(CharSequenceIntMap tokenCoder,
        CharSequence [] allTokenImages, int [] allTokens,
        LanguageModelFactory languageFactory);

    /**
     * Returns remapped token sequence.
     */
    public abstract int [] getTokensNormalized();

}