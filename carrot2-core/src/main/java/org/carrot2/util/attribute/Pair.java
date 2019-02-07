package org.carrot2.util.attribute;

import java.util.Objects;

/**
 * An immutable pair of objects.
 */
final class Pair<I, J>
{
    public final I objectA;
    public final J objectB;

    public Pair(I clazz, J parameter)
    {
        this.objectA = clazz;
        this.objectB = parameter;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Pair))
        {
            return false;
        }

        final Pair<?, ?> other = (Pair<?, ?>) obj;
        return Objects.equals(other.objectA, objectA) &&
               Objects.equals(other.objectB, objectB);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(objectA, objectB);
    }
}
