
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.suffixtrees;

/**
 * The interface providing access to collection elements for use by SuffixTree. In order
 * to be used by SuffixTree class (and subclasses), the object must implement this
 * interface.
 */
public interface ISuffixableElement
{
    /**
     * Static constant marker for end-of-suffix boundary. This object always returns
     * <code>false</code> from its {@link Object#equals(Object)} method.
     */
    public static final Object END_OF_SUFFIX = new Object()
    {
        public boolean equals(Object other)
        {
            if (other == this)
            {
                return true;
            }

            return false;
        }
        
        public int hashCode()
        {
            // Random number, but fixed for this object.
            return 0x11223344;
        }

        public String toString()
        {
            return ("EOS");
        }
    };

    /**
     * Returns an object at specified index in this collection. Object must implement
     * {@link Comparable} interface.
     */
    public Object get(int index);

    /**
     * Returns this collection's length.
     */
    public int size();
}
