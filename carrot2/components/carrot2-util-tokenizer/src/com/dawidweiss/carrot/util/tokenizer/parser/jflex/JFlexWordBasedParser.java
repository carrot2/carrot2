/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.util.tokenizer.parser.jflex;

import java.io.*;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.common.pools.*;
import com.dawidweiss.carrot.util.tokenizer.parser.*;

/**
 * Tokenizer class splits a string into tokens like word, e-mail address, web
 * page address and such. Instances may be quite large (memory consumption-wise)
 * so they should be reused rather than recreated.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class JFlexWordBasedParser extends WordBasedParserBase
{
    /**
     * An instance of JFlex-generated parser that is used for input stream
     * tokenization.
     */
    private JFlexWordBasedParserImpl tokenizer;

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
    public JFlexWordBasedParser()
    {
        super();
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
            int t = tokenizer.getNextToken();

            if (t == JFlexWordBasedParserImpl.YYEOF)
            {
                return null;
            }

            if (tokenTypeHolder != null)
            {
                switch (t)
                {
                    case JFlexWordBasedParserImpl.FULL_URL:
                    case JFlexWordBasedParserImpl.FILE:
                    case JFlexWordBasedParserImpl.EMAIL:
                        tokenTypeHolder[0] = TypedToken.TOKEN_TYPE_SYMBOL;
                        break;

                    case JFlexWordBasedParserImpl.TERM:
                    case JFlexWordBasedParserImpl.HYPHTERM:
                    case JFlexWordBasedParserImpl.ACRONYM:
                    case JFlexWordBasedParserImpl.BARE_URL:
                        tokenTypeHolder[0] = TypedToken.TOKEN_TYPE_TERM;
                        break;

                    case JFlexWordBasedParserImpl.SENTENCEMARKER:
                        tokenTypeHolder[0] = TypedToken.TOKEN_TYPE_PUNCTUATION
                            | TypedToken.TOKEN_FLAG_SENTENCE_DELIM;
                        break;

                    case JFlexWordBasedParserImpl.PUNCTUATION:
                        tokenTypeHolder[0] = TypedToken.TOKEN_TYPE_PUNCTUATION;
                        break;

                    case JFlexWordBasedParserImpl.NUMERIC:
                        tokenTypeHolder[0] = TypedToken.TOKEN_TYPE_NUMERIC;
                        break;

                    default:
                        throw new RuntimeException("Unexpected token type: "
                            + t);
                }
            }

            return tokenizer.yytext();
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
        catch (IOException e)
        {
            throw new RuntimeException("Parser exception: ", e);
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
            tokenizer.yyreset(stream);
        }
        else
        {
            tokenizer = new JFlexWordBasedParserImpl(stream);
            tokenizer.yyreset(stream);
        }
    }
}