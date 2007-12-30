/**
 * 
 */
package org.carrot2.util;

/**
 * Provides a number of useful method operating on generic {@link Object}s, mostly for
 * graceful handling of <code>null</code> values.
 */
public final class ObjectUtils
{
    private ObjectUtils()
    {
    }

    /**
     * Tests whether two objects are equal allowing either or both arguments to be null.
     * 
     * @param o1 first object to test, may be <code>null</code>
     * @param o2 second object to test, may be <code>null</code>
     * @return <code>true</code> if <code>o1</code> and <code>o2</code> are equal
     *         (by means of {@link Object#equals(Object)} or they are both
     *         <code>null</code>. Otherwise, returns <code>false</code>.
     */
    public static boolean equals(Object o1, Object o2)
    {
        if (o1 == null && o2 == null)
        {
            return true;
        }

        if (o1 != null ^ o2 != null)
        {
            return false;
        }

        return o1.equals(o2);
    }
}
