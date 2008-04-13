/**
 *
 */
package org.carrot2.util.attribute.test.binder;

import org.carrot2.util.attribute.*;

/**
 *
 */
@Bindable
public class BindableReferenceImpl2 implements BindableReference
{
    /**
     * Init input int.
     */
    @TestInit
    @Input
    @Attribute
    private int initInputInt = 12;

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof BindableReferenceImpl2))
        {
            return false;
        }

        BindableReferenceImpl2 other = (BindableReferenceImpl2) obj;
        
        return initInputInt == other.initInputInt;
    }

    @Override
    public int hashCode()
    {
        return initInputInt;
    }
}
