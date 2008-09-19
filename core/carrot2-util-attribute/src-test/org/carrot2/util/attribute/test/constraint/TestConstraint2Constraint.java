package org.carrot2.util.attribute.test.constraint;

import java.lang.annotation.Annotation;

import org.carrot2.util.attribute.constraint.Constraint;

public class TestConstraint2Constraint extends Constraint
{
    public int value;

    public TestConstraint2Constraint()
    {
    }

    TestConstraint2Constraint(int value)
    {
        this.value = value;
    }

    protected boolean isMet(Object value)
    {
        return false; // does not matter
    }

    @Override
    protected void populateCustom(Annotation annotation)
    {
        this.value = ((TestConstraint2) annotation).value();
    }
}
