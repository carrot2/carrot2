package org.carrot2.util.attribute.constraint;

/**
 * Implementation of the {@link IntModuloConstraint}.
 */
class IntModuloConstraint extends Constraint
{
    int modulo;
    int offset;

    IntModuloConstraint()
    {
    }

    IntModuloConstraint(int modulo, int offset)
    {
        this.modulo = modulo;
        this.offset = offset;
    }

    boolean isMet(Object value)
    {
        final Integer v = (Integer) value;
        return (v % modulo) == offset;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj == null || !(obj instanceof IntModuloConstraint))
        {
            return false;
        }

        final IntModuloConstraint other = (IntModuloConstraint) obj;

        return other.modulo == this.modulo && other.offset == this.offset;
    }

    @Override
    public int hashCode()
    {
        return offset ^ modulo;
    }

    @Override
    public String toString()
    {
        return "modulo(modulo = " + modulo + ", offset = " + offset + ")";
    }
}
