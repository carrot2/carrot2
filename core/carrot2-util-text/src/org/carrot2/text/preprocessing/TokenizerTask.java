package org.carrot2.text.preprocessing;

import java.util.Collection;

import org.apache.lucene.analysis.Analyzer;
import org.carrot2.core.Document;
import org.carrot2.text.*;

/**
 * Tokenization contract for {@link Preprocessor}. 
 * 
 * @see PreprocessingTasks#TOKENIZE
 */
public interface TokenizerTask
{
    /**
     * Add a collection of {@link Document}s to the list. For each {@link Document}, a
     * given set of fields is inspected and added to the tokenizer stream. Fields are
     * separated with {@link PreprocessingContext#SEPARATOR_FIELD}.
     */
    public abstract void add(Collection<Document> documents,
        Collection<String> documentFields, Analyzer analyzer);

    /**
     * Returns unique images of tokens.
     */
    public abstract MutableCharArray [] getTokenImages();

    /**
     * Returns the array of added token and separator codes.
     */
    public abstract int [] getTokens();

    /**
     * Returns the array of token types, indices in this array correspond to
     * {@link #getTokens()}.
     */
    public abstract int [] getTokenTypes();

    /**
     * @return Return the {@link CharSequenceIntMap} used internally to store unique token
     *         images.
     */
    public abstract CharSequenceIntMap getTokenMap();
}