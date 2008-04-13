package org.carrot2.util;

/**
 * Very simple array of primitive <code>int</code> values,
 * providing just appending functionality (no deletions).
 */
public final class IntArrayBuilder
{
    /**
     * Last saved index in {@link #values}.
     */
    private int currentIndex = 0;

    /**
     * Array of internal values.
     */
    private int [] values;

    /**
     * Append <code>value</code> to the end of the list.
     */
    public void add(int value)
    {
        ensureCapacity(currentIndex + 1);
        values[currentIndex] = value;
        currentIndex++;
    }

    /**
     * Ensure {@link #tokens} can hold index <code>minCapacity</code>.
     */
    private void ensureCapacity(int minCapacity)
    {
        if (values == null)
        {
            values = new int [minCapacity + 1];
            return;
        }

        final int oldCapacity = values.length;
        if (minCapacity > oldCapacity)
        {
            final int oldData[] = values;
            final int newCapacity = Math.max(minCapacity, (oldCapacity * 3) / 2 + 1);

            values = new int [newCapacity];
            System.arraycopy(oldData, 0, values, 0, Math.min(oldData.length,
                values.length));
        }
    }

    /**
     * @return Return the array of integers appended so far.
     */
    public int [] toArray()
    {
        final int [] result = new int [currentIndex];
        if (values != null)
        {
            System.arraycopy(values, 0, result, 0, currentIndex);
        }
        return result;
    }
}
