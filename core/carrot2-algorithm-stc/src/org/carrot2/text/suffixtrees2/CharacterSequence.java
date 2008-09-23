package org.carrot2.text.suffixtrees2;

/**
 * A {@link Sequence} wrapping arbitrary {@link CharSequence}.
 */
public final class CharacterSequence implements Sequence
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
