/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.util.tokenizer.parser.javacc;

import java.io.*;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.common.pools.*;
import com.dawidweiss.carrot.util.tokenizer.parser.*;

/**
 * Tokenizer class splits a string into tokens like word, e-mail address, web
 * page address and such. Instances may be quite large (memory consumption-wise)
 * so they should be reused rather than recreated.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class JavaCCWordBasedParser extends WordBasedParserBase
{
    /**
     * An instance of JavaCC-generated parser that is used for input stream
     * tokenization.
     */
    private JavaCCWordBasedParserImpl tokenizer;

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
    public JavaCCWordBasedParser()
    {
        super();
    }

    /**
     * Creates an instance of the parser that uses a custom pool of token
     * objects. The pool <b>must </b> return objects subclassing
     * {@link StringTypedToken}class.
     * 
     * @param pool An unbounded pool of objects implementing at least
     *            {@link StringTypedToken}interface.
     */
    public JavaCCWordBasedParser(ReusableObjectsPool pool)
    {
        super(pool);
    }

    /**
     * Reuses the tokens pool assigned to this parser. All tokens returned from
     * this parser become invalid and should be no longer referenced after this
     * call.
     */
    public void reuse()
    {
        pool.reuse();
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
    protected String getNextToken(short [] tokenTypeHolder)
    {
        try
        {
            com.dawidweiss.carrot.util.tokenizer.parser.javacc.Token t = tokenizer
                .getNextToken();

            if (t.kind == JavaCCWordBasedParserImplConstants.EOF)
            {
                return null;
            }

            if (tokenTypeHolder != null)
            {
                switch (t.kind)
                {
                    case JavaCCWordBasedParserImplConstants.URL:
                    case JavaCCWordBasedParserImplConstants.EMAIL:
                        tokenTypeHolder[0] = TypedToken.TOKEN_TYPE_SYMBOL;
                        break;

                    case JavaCCWordBasedParserImplConstants.TERM:
                    case JavaCCWordBasedParserImplConstants.HYPHTERM:
                    case JavaCCWordBasedParserImplConstants.ACRONYM:
                        tokenTypeHolder[0] = TypedToken.TOKEN_TYPE_TERM;
                        break;

                    case JavaCCWordBasedParserImplConstants.SENTENCEMARKER:
                        tokenTypeHolder[0] = TypedToken.TOKEN_TYPE_PUNCTUATION
                            | TypedToken.TOKEN_FLAG_SENTENCE_DELIM;
                        break;

                    case JavaCCWordBasedParserImplConstants.PUNCTUATION:
                        tokenTypeHolder[0] = TypedToken.TOKEN_TYPE_PUNCTUATION;
                        break;

                    case JavaCCWordBasedParserImplConstants.NUMERIC:
                        tokenTypeHolder[0] = TypedToken.TOKEN_TYPE_NUMERIC;
                        break;

                    default:
                        throw new RuntimeException(
                            "Unexpected token type: "
                                + JavaCCWordBasedParserImplConstants.tokenImage[t.kind]
                                + " (" + t.image + ")");
                }
            }

            return t.image;
        }
        catch (NullPointerException e)
        {
            // catching exception costs nothing
            if (tokenizer == null)
            {
                throw new RuntimeException("Initialize tokenizer first.");
            }

            throw e;
        }
    }

    /**
     * Restarts tokenization on another stream of characters. The tokens pool is
     * <b>not </b> reused at this point; an explicit call to {@link #reuse()}is
     * needed to achieve this.
     * 
     * @param stream A character stream to restart tokenization on.
     */
    public void restartTokenizationOn(Reader stream)
    {
        if (tokenizer != null)
        {
            tokenizer.ReInit(stream);
        }
        else
        {
            tokenizer = new JavaCCWordBasedParserImpl(stream);
        }
    }
}