package org.carrot2.text.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.carrot2.util.CloseableUtils;

/**
 * A tokenizer separating input characters on whitespace, but capable of extracting more
 * complex tokens, such as URLs, e-mail addresses and sentence delimiters.
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
    private ExtendedWhitespaceTokenizerImpl parser;

    /**
     * Reusable object for returning token type.
     */
    private final TokenInfo tokenInfo = new TokenInfo(
        ExtendedWhitespaceTokenizerImpl.YYEOF);

    /**
     * 
     */
    public ExtendedWhitespaceTokenizer(Reader input)
    {
        reset(input);
    }

    /**
     * Return the next token or <code>null</code> in case the token was not found.
     * {@link Token} instances will be reused in subsequent requests.
     */
    public final Token next(Token result) throws IOException
    {
        final int tokenType = parser.getNextToken();
        tokenInfo.setValue(tokenType);

        // EOF?
        if (tokenType == ExtendedWhitespaceTokenizerImpl.YYEOF)
        {
            return null;
        }

        if (result == null)
        {
            result = new Token();
        }

        // TODO: Add intern() on certain token type values (proliferation
        // of common symbols)? Lucene's Tokens convert it back to char buffers anyway,
        // so maybe there is little point...
        final String tokenText = parser.yytext();
        result.setTermText(tokenText);

        return result;
    }

    /**
     * @return Returns the {@link TokenInfo} of the token last returned from
     *         {@link #next(Token)}.
     */
    public final TokenInfo getLastTokenInfo()
    {
        return this.tokenInfo;
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
        this.parser = new ExtendedWhitespaceTokenizerImpl(input);
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
            this.parser = null;
        }
    }
}
