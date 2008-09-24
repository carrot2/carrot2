package org.carrot2.text.suffixtrees;

/**
 * The interface providing access to collection elements for use by SuffixTree. In order
 * to be used by SuffixTree class (and subclasses), the object must implement this
 * interface.
 * 
 * @author Dawid Weiss
 */
public interface SuffixableElement
{
    /**
     * Static constant marker for end-of-suffix boundary. This object always returns
     * <code>false</code> from its {@link #equals(Object)} method.
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
