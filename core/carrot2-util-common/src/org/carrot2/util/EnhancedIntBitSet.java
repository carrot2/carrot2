/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Stanislaw Osinski, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package org.carrot2.util;

import bak.pcj.IntCollection;
import bak.pcj.set.IntBitSet;

/**
 * An enhanced version of {@link IntBitSet} with a more efficient implementation of some
 * operations. This class implements the {@link #addAll(IntCollection)},
 * {@link #retainAll(IntCollection)} and {@link #removeAll(IntCollection)} methods using
 * bitwise operations rather than the generic {@link #add(int)} and {@link #remove(int)}
 * methods. Please note that the optimized algorithms will be used only if both sets involved
 * in the operation are instances of {@link EnhancedIntBitSet}.
 */
@SuppressWarnings("serial")
public class EnhancedIntBitSet extends IntBitSet
{
    public EnhancedIntBitSet()
    {
        super();
    }

    /**
     * Creates a new {@link EnhancedIntBitSet} with the size appropriate to hold the
     * specified maximum value without resizing.
     */
    public EnhancedIntBitSet(int maximum)
    {
        super(maximum);
    }

    /**
     * Creates a new {@link EnhancedIntBitSet} initialized with the contents of the
     * provided <code>int</code> array.
     */
    public EnhancedIntBitSet(int [] a)
    {
        super(a);
    }

    /**
     * Creates a new {@link EnhancedIntBitSet} initialized with the contents of the
     * provided {@link IntCollection}.
     */
    public EnhancedIntBitSet(IntCollection c)
    {
        super(c);
    }

    /**
     * Creates a new {@link EnhancedIntBitSet} initialized with the contents of the
     * provided {@link EnhancedIntBitSet}.
     */
    public EnhancedIntBitSet(EnhancedIntBitSet s)
    {
        this.data = new long [s.data.length];
        this.size = s.size;
        System.arraycopy(s.data, 0, this.data, 0, s.data.length);
    }

    public boolean addAll(IntCollection c)
    {
        if (c instanceof EnhancedIntBitSet)
        {
            final EnhancedIntBitSet set = (EnhancedIntBitSet) c;
            if (set.size() == 0)
            {
                return false;
            }

            final int updateLength = Math.min(set.data.length, this.data.length);
            final int countLength = Math.max(set.data.length, this.data.length);
            if (set.data.length > this.data.length)
            {
                final long [] newData = new long [set.data.length];
                System.arraycopy(data, 0, newData, 0, data.length);
                data = newData;
            }

            size = 0;
            boolean changed = false;
            for (int i = 0; i < updateLength; i++)
            {
                final long thisData = this.data[i];
                final long setData = set.data[i];
                if (setData != 0 && thisData != setData)
                {
                    data[i] |= setData;

                    if (!changed && thisData != data[i])
                    {
                        changed = true;
                    }
                }
                size += countBits(data[i]);
            }

            if (set.data.length == countLength)
            {
                for (int i = updateLength; i < countLength; i++)
                {
                    final long setData = set.data[i];
                    data[i] = setData;
                    if (setData != 0)
                    {
                        changed = true;
                        size += countBits(setData);
                    }
                }
            }
            else
            {
                for (int i = updateLength; i < countLength; i++)
                {
                    size += countBits(this.data[i]);
                }
            }

            return changed;
        }
        else
        {
            return super.addAll(c);
        }
    }

    /**
     * Returns the size this set would have if {@link #addAll(IntCollection)} was called
     * on it with the provided <code>set</code>. Note that after calling this method, this
     * set will not change. Use this method when you need to determine the size of an
     * union of two sets, but are not interested in the actual content of the union.
     */
    public int addAllSize(EnhancedIntBitSet set)
    {
        final long [] longer = this.data.length > set.data.length ? this.data : set.data;
        final long [] shorter = this.data.length <= set.data.length ? this.data
            : set.data;

        int count = 0;
        for (int i = 0; i < shorter.length; i++)
        {
            count += countBits(longer[i] | shorter[i]);
        }
        for (int i = shorter.length; i < longer.length; i++)
        {
            count += countBits(longer[i]);
        }

        return count;
    }

    /*
     * (non-Javadoc)
     * @see bak.pcj.AbstractIntCollection#removeAll(bak.pcj.IntCollection)
     */
    public boolean removeAll(IntCollection c)
    {
        if (c instanceof EnhancedIntBitSet)
        {
            EnhancedIntBitSet set = (EnhancedIntBitSet) c;
            if (set.size() == 0 || size() == 0)
            {
                return false;
            }

            final int updateLength = Math.min(set.data.length, this.data.length);

            size = 0;
            boolean changed = false;
            for (int i = 0; i < updateLength; i++)
            {
                final long a = data[i];
                final long b = set.data[i];
                data[i] &= ~b;

                if (!changed && data[i] != a)
                {
                    changed = true;
                }

                size += countBits(data[i]);
            }

            for (int i = updateLength; i < this.data.length; i++)
            {
                size += countBits(data[i]);
            }

            return changed;
        }
        else
        {
            return super.removeAll(c);
        }
    }
    
    /**
     * Returns the size this set would have if {@link #removeAll(IntCollection)} was called
     * on it with the provided <code>set</code>. Note that after calling this method, this
     * set will not change. Use this method when you need to determine the size of a
     * difference of two sets, but are not interested in the actual content of the difference.
     */
    public int removeAllSize(EnhancedIntBitSet set)
    {
        int count = 0;
        final int commonLength = Math.min(set.data.length, this.data.length);

        for (int i = 0; i < commonLength; i++)
        {
            count += countBits(this.data[i] & ~set.data[i]);
        }
        
        for (int i = commonLength; i < this.data.length; i++)
        {
            count += countBits(this.data[i]);
        }
        
        return count;
    }

    public boolean retainAll(IntCollection c)
    {
        if (c instanceof EnhancedIntBitSet)
        {
            EnhancedIntBitSet set = (EnhancedIntBitSet) c;
            if (size() == 0)
            {
                return false;
            }

            size = 0;
            boolean changed = false;
            final int min = Math.min(set.data.length, data.length);
            for (int i = 0; i < min; i++)
            {
                final long a = data[i];
                final long b = set.data[i];
                if (a != 0 && a != b)
                {
                    data[i] &= b;

                    if (!changed && data[i] != a)
                    {
                        changed = true;
                    }

                    size += countBits(data[i]);
                }
                else
                {
                    size += countBits(a);
                }
            }

            for (int i = set.data.length; i < data.length; i++)
            {
                data[i] = 0;
            }

            return changed;
        }
        else
        {
            return super.retainAll(c);
        }
    }
    
    /**
     * Returns the size this set would have if {@link #retainAll(IntCollection)} was called
     * on it with the provided <code>set</code>. Note that after calling this method, this
     * set will not change. Use this method when you need to determine the size of an
     * intersection of two sets, but are not interested in the actual content of the intersection.
     */
    public int retainAllSize(EnhancedIntBitSet set)
    {
        int count = 0;
        final int commonLength = Math.min(set.data.length, this.data.length);

        for (int i = 0; i < commonLength; i++)
        {
            count += countBits(this.data[i] & set.data[i]);
        }
        
        return count;
    }
}
