/**
 *
 */
package carrot2.util.attribute.constraint;


@SuppressWarnings("serial")
public class ConstraintViolationException extends RuntimeException
{
    private final Object offendingValue;
    private final Constraint constraint;
    private final String key;

    public ConstraintViolationException(String key, Constraint constraint,
        Object offendngValue)
    {
        this.offendingValue = offendngValue;
        this.constraint = constraint;
        this.key = key;
    }

    @Override
    public String getMessage()
    {
        return "Value: " + offendingValue + " of attribute: " + key
            + " violates constraint: " + constraint;
    }

    public Object getOffendingValue()
    {
        return offendingValue;
    }

    public Constraint getConstraint()
    {
        return constraint;
    }

    public String getKey()
    {
        return key;
    }
}
