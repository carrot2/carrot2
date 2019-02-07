
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.test.assertions;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.fest.assertions.Condition;
import org.fest.assertions.ItemGroupAssert;

/**
 * A simple base class for generic list asserts.
 */
public class GenericListAssertion<S, E> extends ItemGroupAssert<S, List<E>>
{
    GenericListAssertion(Class<S> selfClass, List<E> actual)
    {
        super(selfClass, actual);
    }

    public S allSatisfy(final Condition<E> condition)
    {
        return this.satisfies(new Condition<List<E>>()
        {
            @Override
            public boolean matches(List<E> list)
            {
                for (E element : list)
                {
                    if (!condition.matches(element))
                    {
                        return false;
                    }
                }
                return true;
            }
        });
    }

    public S anySatisfies(final Condition<E> condition)
    {
        return this.satisfies(new Condition<List<E>>()
        {
            @Override
            public boolean matches(List<E> list)
            {
                for (E element : list)
                {
                    if (condition.matches(element))
                    {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected List<Object> actualAsList()
    {
        return Collections.singletonList(actual);
    }

    @Override
    protected Set<Object> actualAsSet()
    {
        return Collections.singleton(actual);
    }

    @Override
    protected int actualGroupSize()
    {
        isNotNull();
        return actual.size();
    }
}
