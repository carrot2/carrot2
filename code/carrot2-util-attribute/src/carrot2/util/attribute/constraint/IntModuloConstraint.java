/**
 *
 */
package carrot2.util.attribute.constraint;

/**
 *
 */
public class IntModuloConstraint implements Constraint
{
    private int modulo;
    private int offset;

    public IntModuloConstraint()
    {
    }

    public IntModuloConstraint(int modulo, int offset)
    {
        this.modulo = modulo;
        this.offset = offset;
    }

    public boolean isMet(Object value)
    {
        final Integer v = (Integer) value;
        return (v % modulo) == offset;
    }

    public int getModulo()
    {
        return modulo;
    }

    public int getOffset()
    {
        return offset;
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
