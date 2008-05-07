package org.carrot2.text.suffixtrees2;

/**
 * A {@link Sequence} wrapping arbitrary {@link CharSequence}.
 */
public final class CharacterSequence implements Sequence
{
    private final CharSequence seq;

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
