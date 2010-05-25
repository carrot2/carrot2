package org.carrot2.text.preprocessing;

/**
 * A special-purpose map backed by double-linked set of <code>int</code> values. 
 */
final class IntIntDoubleLinkedMap 
{
    /**
     * Dense array of set elements. 
     */
    private final int [] dense;

    /**
     * Sparse, element value-indexed array pointing back at {@link #dense}.
     */
    private final int [] sparse;

    /**
     * Values associated with keys at indexes aligned with {@link #dense}.
     */
    private final int [] values;

    /**
     * Current number of elements stored in the set ({@link #dense}).
     */
    private int elementsCount;

    /**
     * Create with a custom dense array resizing strategy.
     */
    public IntIntDoubleLinkedMap(int denseCapacity, int sparseCapacity)
    {
        assert denseCapacity >= 0 : "denseCapacity must be >= 0: " + denseCapacity;
        assert sparseCapacity >= 0 : "sparseCapacity must be >= 0: " + sparseCapacity;

        dense = new int [denseCapacity];
        values = new int [denseCapacity];
        sparse = new int [sparseCapacity];
    }

    public int size()
    {
        return elementsCount;
    }

    public boolean isEmpty()
    {
        return size() == 0;
    }

    public void clear()
    {
        this.elementsCount = 0;
    }

    public void putOrAdd(int key, int value, int defaultValue)
    {
        final int index = sparse[key];
        if (index < elementsCount && dense[index] == key)
        {
            values[index] += value;
        }
        else
        {
            sparse[key] = elementsCount;
            dense[elementsCount] = key;
            values[elementsCount++] = defaultValue;
        }
    }

    public int [] toArray() 
    {
        final int [] result = new int [size() * 2];
        final int [] dense = this.dense;
        final int [] values = this.values;

        for (int j = 0, i = 0; i < elementsCount; i++)
        {
            result[j++] = dense[i];
            result[j++] = values[i];
        }

        return result;
    }
}
