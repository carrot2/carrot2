
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.carrot2.util.CloseableUtils;

/**
 * A tokenizer separating input characters on whitespace, but capable of extracting more
 * complex tokens, such as URLs, e-mail addresses and sentence delimiters. Provides
 * {@link TermAttribute}s and {@link TokenTypeAttributeImpl}s implementing {@link ITokenTypeAttribute}.
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

    private final TermAttribute term;
    private final ITokenTypeAttribute type;

    public ExtendedWhitespaceTokenizer()
    {
        parser = new ExtendedWhitespaceTokenizerImpl(input);
        super.addAttributeImpl(new TokenTypeAttributeImpl());
        type = addAttribute(ITokenTypeAttribute.class);
        term = addAttribute(TermAttribute.class);
    }
    
    @Override
    public boolean incrementToken() throws IOException
    {
        super.clearAttributes();

        final int tokenType = parser.getNextToken();

        // EOF?
        if (tokenType == ExtendedWhitespaceTokenizerImpl.YYEOF)
        {
            return false;
        }

        type.setRawFlags((short) tokenType);
        term.setTermBuffer(parser.yybuffer(), parser.yystart(), parser.yylength());
        term.setTermLength(parser.yylength());
        return true;
    }

    /**
     * Not implemented in this tokenizer. Use {@link #reset(Reader)} or {@link #close()}.
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

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof ExtendedWhitespaceTokenizer)
        {
            return super.equals(other);
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        // Just to document we're fine with using AttributeSource.hashCode()
        return super.hashCode();
    }
}
