/**
 * 
 */
package org.carrot2.core.constraint;

import org.carrot2.core.parameter.ParameterDescriptor;

@SuppressWarnings("serial")
public class ConstraintViolationException extends RuntimeException
{
    private final Object offendingValue;
    private final Constraint constraint;
    private final ParameterDescriptor parameterDescriptor;

    public ConstraintViolationException(ParameterDescriptor parameterDescriptor,
        Constraint constraint, Object offendngValue)
    {
        this.offendingValue = offendngValue;
        this.constraint = constraint;
        this.parameterDescriptor = parameterDescriptor;
    }

    @Override
    public String getMessage()
    {
        return "Value: " + offendingValue + " of parameter: "
            + parameterDescriptor.getKey() + " violates constraint: " + constraint;
    }

    public Object getOffendingValue()
    {
        return offendingValue;
    }

    public Constraint getConstraint()
    {
        return constraint;
    }

    public ParameterDescriptor getParameterDescriptor()
    {
        return parameterDescriptor;
    }
}
