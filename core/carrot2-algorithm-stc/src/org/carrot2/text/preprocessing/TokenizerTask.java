package org.carrot2.text.preprocessing;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.*;
import org.carrot2.core.Document;
import org.carrot2.text.*;
import org.carrot2.text.analysis.*;
import org.carrot2.util.*;

/**
 * {@link Document} tokenizer. Contributes {@link PreprocessingContext#tokens} table.
 */
public final class TokenizerTask
{
    /**
     * Current token's image (for token ID lookups).
     */
    private final MutableCharArray currentToken = new MutableCharArray("");

    /**
     * A map of codes for previously seen token images.
     */
    private final CharSequenceIntMap tokenMap;

    /**
     * An array of unique token images.
     */
    private final IntArrayBuilder tokens = new IntArrayBuilder();

    /**
     * An array of token types.
     * 
     * @see TokenType
     */
    private final IntArrayBuilder tokenTypes = new IntArrayBuilder();

    /**
     * 
     */
    public TokenizerTask(CharSequenceIntMap tokenMap)
    {
        this.tokenMap = tokenMap;
    }

    /**
     * Add a collection of {@link Document}s to the list. For each {@link Document}, a
     * given set of fields is inspected and added to the tokenizer stream. Fields are
     * separated with {@link PreprocessingContext#SEPARATOR_FIELD}.
     */
    public void add(Collection<Document> documents, Collection<String> documentFields,
        Analyzer analyzer)
    {
        final ArrayList<String> fieldValues = new ArrayList<String>();

        final Iterator<Document> docIterator = documents.iterator();
        while (docIterator.hasNext())
        {
            final Document doc = docIterator.next();

            // Queue all non-empty document fields for this document.
            fieldValues.clear();
            for (String fieldName : documentFields)
            {
                final String value = doc.getField(fieldName);
                if (!StringUtils.isEmpty(value))
                {
                    fieldValues.add(value);
                }
            }

            // Tokenize all non-empty document fields for this document.
            Token t = null;
            try
            {
                while (!fieldValues.isEmpty())
                {
                    final TokenStream ts = analyzer.reusableTokenStream(null,
                        new StringReader(fieldValues.remove(0)));

                    while ((t = ts.next(t)) != null)
                    {
                        add(t);
                    }

                    // Split fields with a sentence break.
                    if (!fieldValues.isEmpty())
                    {
                        add(PreprocessingContext.SEPARATOR_FIELD,
                            TokenType.TF_SEPARATOR_FIELD);
                    }
                }
            }
            catch (IOException e)
            {
                // Not possible (StringReader above)?
                throw ExceptionUtils.wrapAs(RuntimeException.class, e);
            }
            catch (ClassCastException e)
            {
                throw new RuntimeException("The analyzer must provide "
                    + TokenType.class.getName() + " instances as payload.");
            }

            if (docIterator.hasNext())
            {
                add(PreprocessingContext.SEPARATOR_DOCUMENT,
                    TokenType.TF_SEPARATOR_DOCUMENT);
            }
        }
    }

    /**
     * Add the token's code to the list. The <code>token</code> must carry
     * {@link TokenType} payload.
     */
    public void add(Token token)
    {
        final TokenType type = (TokenType) token.getPayload();

        if (TokenTypeUtils.isSentenceDelimiter(type))
        {
            add(PreprocessingContext.SEPARATOR_SENTENCE, type.getRawFlags());
        }
        else
        {
            currentToken.reset(token.termBuffer(), 0, token.termLength());
            add(tokenMap.getIndex(currentToken), type.getRawFlags());
        }
    }

    /**
     * Adds custom token code to the sequence. May be used to add separator constants.
     */
    public void add(int tokenCode, int tokenTypeCode)
    {
        tokenTypes.add(tokenTypeCode);
        tokens.add(tokenCode);
    }

    /**
     * Returns the array of added token and separator codes.
     */
    public int [] getTokens()
    {
        return tokens.toArray();
    }

    /**
     * Returns unique images of tokens.
     */
    public MutableCharArray [] getTokenImages()
    {
        return tokenMap.getTokenImages();
    }

    /**
     * Returns the array of token types, indices in this array correspond
     * to {@link #getTokens()}.
     */
    public int [] getTokenTypes()
    {
        return tokenTypes.toArray();
    }
}
