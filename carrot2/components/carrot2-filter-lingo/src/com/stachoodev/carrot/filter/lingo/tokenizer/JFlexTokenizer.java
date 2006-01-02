
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
package com.stachoodev.carrot.filter.lingo.tokenizer;

import java.io.*;


/**
 * Tokenizer class splits a string into tokens like word, e-mail address, web
 * page address and such. Instances may be quite large (memory
 * consumption-wise) so they should be reused rather than recreated.
 */
public class JFlexTokenizer implements Tokenizer {
    /** DOCUMENT ME! */
    private JFlexWordBasedParserImpl tokenizer;

    /**
     * Use factory method to acquire instances of this class.
     */
    private JFlexTokenizer() {
    }

    /**
     * Creates a new empty tokenizer, which has to be initialized using one of
     * the restart methods.
     */
    public static Tokenizer getTokenizer() {
        return new JFlexTokenizer();
    }

    /**
     * Restart token parsing for input string <code>string</code>.
     */
    public final void restartTokenizerOn(String string) {
        if (tokenizer != null) {
            tokenizer.yyreset(new StringReader(string));
        } else {
            tokenizer = new JFlexWordBasedParserImpl(new StringReader(string));
        }
    }

    /**
     * Return the next token from the parsing data. The token value is returned
     * from the method, while the type of the token is stored in the zero
     * index of the input parameter <code>tokenTypeHolder</code>. If
     * tokenTypeHolder is <code>null</code>, token type is not saved anywhere.
     * <code>null</code> is returned when end of the input data has been
     * reached. This method is <em>not</em> synchronized.
     */
    public final String getNextToken(int[] tokenTypeHolder) {
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
                    case JFlexWordBasedParserImpl.EMAIL:
                        tokenTypeHolder[0] = TYPE_EMAIL;
                        break;
                        
                    case JFlexWordBasedParserImpl.FULL_URL:
                    case JFlexWordBasedParserImpl.FILE:
                        tokenTypeHolder[0] = TYPE_URL;
                        break;

                    case JFlexWordBasedParserImpl.TERM:
                    case JFlexWordBasedParserImpl.HYPHTERM:
                    case JFlexWordBasedParserImpl.ACRONYM:
                    case JFlexWordBasedParserImpl.BARE_URL:
                        tokenTypeHolder[0] = TYPE_TERM;
                        break;

                    case JFlexWordBasedParserImpl.SENTENCEMARKER:
                        tokenTypeHolder[0] = TYPE_SENTENCEMARKER;
                        break;

                    case JFlexWordBasedParserImpl.PUNCTUATION:
                        tokenTypeHolder[0] = TYPE_PUNCTUATION;
                        break;

                    case JFlexWordBasedParserImpl.NUMERIC:
                        tokenTypeHolder[0] = TYPE_NUMERIC;
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
}
