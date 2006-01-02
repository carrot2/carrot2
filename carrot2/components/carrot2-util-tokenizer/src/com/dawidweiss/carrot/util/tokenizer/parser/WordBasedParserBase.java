
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.util.tokenizer.parser;

import com.dawidweiss.carrot.core.local.linguistic.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.Token;
import com.dawidweiss.carrot.util.common.pools.*;
import com.dawidweiss.carrot.util.tokenizer.languages.*;

/**
 * Tokenizer class splits a string into tokens like word, e-mail address, web
 * page address and such. Instances may be quite large (memory consumption-wise)
 * so they should be reused rather than recreated.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public abstract class WordBasedParserBase implements LanguageTokenizer
{
    /**
     * Temporary variable for holding token type passed from the
     * {@link WordBasedParserImpl}.
     */
    protected final short [] tokenTypeHolder = new short [1];

    /**
     * Public constructor creates a new instance of the parser. <b>Reuse
     * tokenizer objects </b> instead of recreating them.
     * 
     * <p>
     * This constructor creates a default {@link SoftReusableObjectsPool}that
     * produces and pools objects of type {@link TypedToken}. If a more
     * specific token type is needed, pass an instance of your own token factory
     * object.
     */
    public WordBasedParserBase()
    {
    }

    /**
     * Reuses the tokens pool assigned to this parser. All tokens returned from
     * this parser become invalid and should be no longer referenced after this
     * call.
     */
    public void reuse()
    {
    }

    /**
     * Returns the next token from the parsing data. The token value is returned
     * from the method, while the type of the token, as defined in
     * {@link com.dawidweiss.carrot.core.local.linguistic.tokens.TypedToken},
     * is stored in the zero index of the input parameter
     * <code>tokenTypeHolder</code>. If tokenTypeHolder is <code>null</code>,
     * token type is not saved anywhere. <code>null</code> is returned when
     * end of the input data has been reached. This method is <em>not</em>
     * synchronized.
     * 
     * @param tokenTypeHolder A holder where the next token's type is saved (at
     *            index 0).
     * @return Returns the next token's value as a String object, or
     *         <code>null</code> if end of the input data has been reached.
     * 
     * @see com.dawidweiss.carrot.core.local.linguistic.tokens.TypedToken
     */
    protected abstract String getNextToken(short [] tokenTypeHolder);

    /**
     * Parses the input and returns a new chunk of tokens.
     * 
     * @param array An array where new tokens will be stored.
     * @param startAt The first index in <code>array</code> to use.
     * @return the number of tokens placed in the array, or 0 if no more tokens
     *         are available.
     */
    public int getNextTokens(Token [] array, int startAt)
    {
        try
        {
            int count = 0;
            String image;
            StringTypedToken token;
            while (startAt < array.length)
            {
                image = getNextToken(tokenTypeHolder);
                if (image == null)
                {
                    break;
                }

                token = new MutableStemmedToken();
                token.assign(image, tokenTypeHolder[0]);
                array[startAt] = token;
                count++;
                startAt++;
            }

            return count;

        }
        catch (ClassCastException e)
        {
            throw new RuntimeException(
                "Class cast exception: invalid object type returned from the pool?",
                e);
        }
        catch (NullPointerException e)
        {
            if (array == null)
            {
                throw new IllegalArgumentException("Array must not be null.");
            }
            else
                throw e;
        }
    }
}