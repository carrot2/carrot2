package org.carrot2.util.attribute.constraint;

import org.simpleframework.xml.Root;

@Root(name = "value-hint")
public class ValueHintEnumConstraint extends Constraint
{
    /*
     * 
     */
    ValueHintEnumConstraint()
    {
        // Hide from the public view.
    }

    /*
     * 
     */
    protected boolean isMet(Object value)
    {
        checkAssignableFrom(CharSequence.class, value);

        /*
         * This is a hint-constraint, so we always return true.
         */
        return true;
    }

    /*
     * 
     */
    @Override
    public String toString()
    {
        return "value-hint";
    }
}
