
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

package org.carrot2.text.suffixtrees2;

/**
 * Converts a {@link ISequence} to a {@link String} using {@link SequenceFormatter}.
 */
public abstract class SequenceFormatter
{
    
    /**
     * Converts a given {@link ISequence} to a {@link String} as if it were characters.
     * Negative characters are given special opcodes: <code>$N</code> where N is a number
     * starting from 0.
     */
    public static String asString(ISequence sequence, SequenceFormatter formatter)
    {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < sequence.size(); i++)
        {
            final int code = sequence.objectAt(i);
            formatter.append(builder, code);
        }
        
        return builder.toString();
    }

    /**
     * Override to provide special formatting of sequence elements.
     */
    protected void append(StringBuilder builder, int code)
    {
        if (builder.length() > 0) builder.append(", ");
        builder.append(code);
    }
}
