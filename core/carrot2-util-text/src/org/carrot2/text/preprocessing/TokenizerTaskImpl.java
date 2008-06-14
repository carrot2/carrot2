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
 * {@link Document} tokenizer.
 * 
 * @see PreprocessingTasks#TOKENIZE
 */
public final class TokenizerTaskImpl implements TokenizerTask
{
    /**
     * Current token's image (for token ID lookups).
     */
    private final MutableCharArray currentToken = new MutableCharArray("");

    /**
     * A map of codes for previously seen token images.
     */
    private CharSequenceIntMap tokenMap;

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
    public TokenizerTaskImpl()
    {
        this.tokenMap = new CharSequenceIntMap();
    }

    /* 
     *
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

    /* 
     *
     */
    public int [] getTokens()
    {
        return tokens.toArray();
    }

    /* 
     *
     */
    public MutableCharArray [] getTokenImages()
    {
        return tokenMap.getTokenImages();
    }

    /* 
     *
     */
    public int [] getTokenTypes()
    {
        return tokenTypes.toArray();
    }

    /* 
     *
     */
    public CharSequenceIntMap getTokenMap()
    {
        return tokenMap;
    }
}
