
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
 * A {@link ISequence} wrapping arbitrary {@link CharSequence}.
 */
public final class CharacterSequence implements ISequence
{
    private final CharSequence seq;
    
    public final static SequenceFormatter FORMATTER = new SequenceFormatter()
    {
        @Override
        protected void append(StringBuilder builder, int code)
        {
            if (code < 0)
            {
                builder.append('$').append(-(code + 1));
            }
            else
            {
                builder.append((char) code);
            }            
        }
    };

    public CharacterSequence(CharSequence chs)
    {
        this.seq = chs;
    }

    public int size()
    {
        return seq.length();
    }

    public int objectAt(int i)
    {
        return seq.charAt(i);
    }
}
