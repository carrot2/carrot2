package org.carrot2.text.suffixtrees;

/**
 * A {@link SuffixableElement} wrapper around a {@link CharSequence}.
 */
final class SuffixableCharacterSequence implements SuffixableElement
{
    private final Character [] chars;
    private final String asString;

    public SuffixableCharacterSequence(CharSequence chs)
    {
        this.chars = new Character [chs.length()];
        for (int i = chs.length() - 1; i >= 0; i--)
        {
            chars[i] = chs.charAt(i);
        }
        this.asString = chs.toString();
    }

    public Object get(int index)
    {
        return chars[index];
    }

    public int size()
    {
        return chars.length;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj instanceof SuffixableCharacterSequence)
        {
            return asString.equals((SuffixableCharacterSequence) obj);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return asString.hashCode();
    }
}
