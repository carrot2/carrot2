

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


import java.util.*;


/**
 * This represents a single HTML tag. Each TagToken has a name and a list of attributes and values.
 *
 * @author <a href="http://www.strath.ac.uk/~ras97108/">David McNicol</a>
 *
 * @see HTMLTokenizer
 */
public class TagToken
{
    /** Identifies the escape character. */
    public static final char ESCAPE = '\\';

    /** Identifies the quotation character. */
    public static final char QUOTE = '"';

    /** Stores the name of the TagToken. */
    private String name;

    /** Indicates whether the TagToken is an end-token. */
    private boolean end = false;

    /** Stores a list of attributes and their values. */
    private AttributeList attr;

    /**
     * Constructs a new TagToken converting the specified string into a token name and a list of
     * attributes with values.
     *
     * @param line the raw data.
     */
    public TagToken(String line)
    {
        name = null;
        attr = new AttributeList();
        tokenizeAttributes(line);
    }

    /**
     * Returns the name of the TagToken.
     */
    public String getName()
    {
        return name;
    }


    /**
     * Returns the attribute list of the TagToken.
     */
    public AttributeList getAttributes()
    {
        return attr;
    }


    /**
     * Indicates whether this token is an end tag.
     */
    public boolean isEndTag()
    {
        return end;
    }


    /**
     * Returns true if the given attribute exists.
     *
     * @param name the name of the attribute.
     */
    public boolean isAttribute(String name)
    {
        return attr.exists(name);
    }


    /**
     * Returns the value of the specified attribute or null if the attribute does not exist.
     *
     * @param name the name of the attribute.
     */
    public String getAttribute(String name)
    {
        return attr.get(name);
    }


    /**
     * Returns an attribute with all double quote characters escaped with a backslash.
     *
     * @param name the name of the attribute.
     */
    public String getQuotedAttribute(String name)
    {
        // Check that the attribute list is there.
        if (attr == null)
        {
            return null;
        }

        // Return the quoted version.
        return attr.getQuoted(name);
    }


    /**
     * Returns a string version of the attribute and its value.
     *
     * @param name the name of the attribute.
     */
    public String getAttributeToString(String name)
    {
        // Check that the attribute list is there.
        if (attr == null)
        {
            return null;
        }

        // Return the string version.
        return attr.toString(name);
    }


    /**
     * Returns a string version of the TagToken.
     */
    public String toString()
    {
        StringBuffer sb; // Stores the string to be returned.
        Enumeration list; // List of node's arguments or children.

        // Get a new StringBuffer.
        sb = new StringBuffer();

        // Write the opening of the tag.
        if (end)
        {
            sb.append("</" + name);
        }
        else
        {
            sb.append('<' + name);

            // Check if there are any attributes.
        }

        if ((attr != null) && (attr.size() > 0))
        {
            // Print string version of the attributes.
            sb.append(' ').append(attr.toString());
        }

        // Finish off the tag.
        sb.append('>');

        // Return the string version.
        return sb.toString();
    }


    /**
     * Sets the name of the token and also whether it is a begin or an end token.
     *
     * @param name the name of the token.
     */
    private void setName(String name)
    {
        if (name == null)
        {
            this.name = null;

            return;
        }

        String lcname = name.toLowerCase();

        if (lcname.charAt(0) == '/')
        {
            this.name = lcname.substring(1);
            end = true;
        }
        else
        {
            this.name = lcname;
        }
    }


    /**
     * Adds a attribute and value to the list.
     *
     * @param name the name of the attribute.
     * @param value the value of the attribute.
     */
    private void setAttribute(String name, String value)
    {
        attr.set(name, value);
    }


    /**
     * Adds a attribute to the list using the given string. The string may either be in the form
     * 'attribute' or 'attribute=value'.
     *
     * @param s contains the attribute information.
     */
    private void setAttribute(String s)
    {
        int idx; // The index of the = sign in the string.
        String name; // Stores the name of the attribute.
        String value; // Stores the value of the attribute.

        // Check if the string is null.
        if (s == null)
        {
            return;
        }

        // Get the index of = within the string.
        idx = s.indexOf('=');

        // Check if there was '=' character present.
        if (idx < 0)
        {
            // If not, add the whole string as the attribute
            // name with a null value.
            setAttribute(s, "");
        }
        else
        {
            // If so, split the string into a name and value.
            name = s.substring(0, idx);
            value = s.substring(idx + 1);

            // Add the name and value to the attribute list.
            setAttribute(name, value);
        }
    }


    /**
     * Tokenizes the given string and uses the resulting vector to to build up the TagToken's
     * attribute list.
     *
     * @param args the string to tokenize.
     */
    private void tokenizeAttributes(String args)
    {
        Vector v; // Vector of tokens from the string.
        Enumeration e; // Enumeration of vector elements.
        String [] tokens = null; // Array of tokens from vector.
        int length; // Size of the vector.
        int i; // Loop variable.

        // Get the vector of tokens.
        v = tokenizeString(args);

        // Check it is not null.
        if (v == null)
        {
            return;
        }

        // Create a new String array.
        e = v.elements();

        // Store the first element as the TagToken's name.
        String n = (String) e.nextElement();

        if (n.startsWith("!--"))
        {
            setName("!--");
            n.replaceFirst("!--", "");
        }
        else
        {
            setName(n);
            n = null;
        }

        length = v.size() - ((n == null) ? 1
                                         : 0);

        if (length > 0)
        {
            tokens = new String[length];

            // Get an enumeration of the vector's elements.
        }

        i = 0;

        if (n != null)
        {
            tokens[i++] = n;
        }

        // Stop processing now if there are no more elements.
        if (!e.hasMoreElements())
        {
            return;
        }

        // Put the rest of the elements into the string array.
        while (e.hasMoreElements())
        {
            tokens[i++] = (String) e.nextElement();

            // Deal with the name/value pairs with separate = signs.
        }

        for (i = 1; i < (length - 1); i++)
        {
            if (tokens[i] == null)
            {
                continue;
            }

            if (tokens[i].equals("="))
            {
                setAttribute(tokens[i - 1], tokens[i + 1]);
                tokens[i] = null;
                tokens[i - 1] = null;
                tokens[i + 1] = null;
            }
        }

        // Deal with lone attributes and joined name/value pairs.
        for (i = 0; i < length; i++)
        {
            if (tokens[i] != null)
            {
                setAttribute(tokens[i]);
            }
        }
    }


    /**
     * This method tokenizes the given string and returns a vector of its constituent tokens. It
     * understands quoting and character escapes.
     *
     * @param s the string to tokenize.
     */
    private Vector tokenizeString(String s)
    {
        // First check that the args are not null or zero-length.
        if ((s == null) || (s.length() == 0))
        {
            return null;
        }

        boolean whitespace = false; // True if we are reading w/space.
        boolean escaped = false; // True if next char is escaped.
        boolean quoted = false; // True if we are in quotes.
        int length; // Length of attribute string.
        int i = 0; // Loop variable.

        // Create a vector to store the complete tokens.
        Vector tokens = new Vector();

        // Create a buffer to store an individual token.
        StringBuffer buffer = new StringBuffer(80);

        // Convert the String to a character array;
        char [] array = s.toCharArray();

        length = array.length;

        // Loop over the character array.
        while (i < length)
        {
            // Check if we are currently removing whitespace.
            if (whitespace)
            {
                if (isWhitespace(array[i]))
                {
                    i++;

                    continue;
                }
                else
                {
                    whitespace = false;
                }
            }

            // Check if we are currently escaped.
            if (escaped)
            {
                // Add the next character to the array.
                buffer.append(array[i++]);

                // Turn off the character escape.
                escaped = false;

                continue;
            }
            else
            {
                // Check for the escape character.
                if (array[i] == ESCAPE)
                {
                    escaped = true;
                    i++;

                    continue;
                }

                // Check for the quotation character.
                if (array[i] == QUOTE)
                {
                    quoted = !quoted;
                    i++;

                    continue;
                }

                // Check for the end of the token.
                if (!quoted && isWhitespace(array[i]))
                {
                    // Add the token and refresh the buffer.
                    tokens.addElement(buffer.toString());
                    buffer = new StringBuffer(80);

                    // Stop reading the token.
                    whitespace = true;

                    continue;
                }

                // Otherwise add the character to the buffer.
                buffer.append(array[i++]);
            }
        }

        // Add the last token to the vector if there is one.
        if (!whitespace)
        {
            tokens.addElement(buffer.toString());
        }

        return tokens;
    }


    /**
     * Returns true if the given character is considered to be whitespace.
     *
     * @param c the character to test.
     */
    private boolean isWhitespace(char c)
    {
        return ((c == ' ') || (c == '\t') || (c == '\n'));
    }


    public void openingTag(StringBuffer sb)
    {
        // Write the opening of the tag.
        sb.append('<');

        // Write the tag's name.
        sb.append(name);

        // Check if there are any attributes.
        if ((attr != null) && (attr.size() > 0))
        {
            // Print string version of the attributes.
            sb.append(" " + attr);
        }

        // Finish off the tag.
        sb.append('>');
    }


    public void closingTag(StringBuffer sb)
    {
        // Write the end tag.
        sb.append("</").append(name).append(">");
    }
}
