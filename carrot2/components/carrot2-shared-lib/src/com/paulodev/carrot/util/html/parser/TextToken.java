

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


/*
 * HTML Parser
 * Copyright (C) 1997 David McNicol
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * file COPYING for more details.
 */
package com.paulodev.carrot.util.html.parser;


/**
 * This represents a block of text.
 *
 * @author <a href="http://www.strath.ac.uk/~ras97108/">David McNicol</a>
 *
 * @see HTMLTokenizer
 */
public class TextToken
{
    /** The content of the token. */
    private StringBuffer text;

    /**
     * Constructs a new token.
     */
    public TextToken()
    {
        text = new StringBuffer();
    }

    /**
     * Sets the content of the Token.
     *
     * @param newText the new content of the Token.
     */
    public void setText(String newText)
    {
        // Replace the old StringBuffer with a new one.
        text = new StringBuffer(newText);
    }


    /**
     * Sets the content of the Token.
     *
     * @param newText the new content of the Token.
     */
    public void setText(StringBuffer newText)
    {
        // Replace the old StringBuffer with a new one.
        text = newText;
    }


    /**
     * Appends some content to the token.
     *
     * @param more the new content to add.
     */
    public void appendText(String more)
    {
        text.append(more);
    }


    /**
     * Returns the contents of the token.
     */
    public StringBuffer getText()
    {
        return text;
    }


    /**
     * Returns a string version of the TextToken.
     */
    public String toString()
    {
        return text.toString();
    }


    public void openingTag(StringBuffer sb)
    {
        sb.append(text.toString());
    }
}
