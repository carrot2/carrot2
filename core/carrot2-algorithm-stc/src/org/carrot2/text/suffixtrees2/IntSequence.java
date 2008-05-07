package org.carrot2.text.suffixtrees2;

/**
 * A {@link Sequence} wrapping arbitrary array of integers.
 */
public final class IntSequence implements Sequence
{
    private final int [] seq;
    private final int start;
    private final int length;

    public IntSequence(int [] seq)
    {
        this(seq, 0, seq.length);
    }

    public IntSequence(int [] seq, int start, int length)
    {
        this.seq = seq;
        this.start = start;
        this.length = length;
    }

    public int size()
    {
        return length;
    }

    public int objectAt(int i)
    {
        return seq[start + i];
    }
}
