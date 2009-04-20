
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.carrot2.util.CloseableUtils;

/**
 * A tokenizer separating input characters on whitespace, but capable of extracting more
 * complex tokens, such as URLs, e-mail addresses and sentence delimiters. For each
 * returned {@link Token}, a payload implementing {@link ITokenType} is returned.
 */
public final class ExtendedWhitespaceTokenizer extends Tokenizer
{
    /**
     * Character stream source.
     */
    private Reader reader;

    /**
     * JFlex parser used to split the input into tokens.
     */
    private final ExtendedWhitespaceTokenizerImpl parser;

    /**
     * Reusable object for returning token type.
     */
    private final TokenTypePayload tokenPayload = new TokenTypePayload();

    /**
     * 
     */
    public ExtendedWhitespaceTokenizer(Reader input)
    {
        this.parser = new ExtendedWhitespaceTokenizerImpl(input);
        reset(input);
    }

    /**
     * Return the next token or <code>null</code> in case the token was not found.
     * {@link Token} instances will be reused in subsequent requests.
     */
    public final Token next(Token result) throws IOException
    {
        assert result != null : "Reusable token must not be null.";

        final int tokenType = parser.getNextToken();

        // EOF?
        if (tokenType == ExtendedWhitespaceTokenizerImpl.YYEOF)
        {
            return null;
        }

        result.setPayload(tokenPayload);
        tokenPayload.setRawFlags(tokenType);

        result.setTermBuffer(parser.yybuffer(), parser.yystart(), parser.yylength());

        return result;
    }

    /**
     * Not implemented in this tokenizer. Use {@link #reset()} or {@link #close()}.
     */
    public void reset() throws IOException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Reset this tokenizer to start parsing another stream.
     */
    public void reset(Reader input)
    {
        if (this.reader != null)
        {
            try
            {
                close();
            }
            catch (IOException e)
            {
                // Fall through, nothing to be done here.
            }
        }

        this.reader = input;
        this.parser.yyreset(input);
    }

    /**
     * 
     */
    public void close() throws IOException
    {
        if (reader != null)
        {
            CloseableUtils.close(reader);
            this.reader = null;
        }
    }
}
