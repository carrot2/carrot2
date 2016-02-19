
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.pool;


/**
 * Test cases for {@link SoftUnboundedPool}.
 */
public class SoftUnboundedPoolTest extends ParameterizedPoolTestBase
{
    protected IParameterizedPool<Object, String> createPool()
    {
        return new SoftUnboundedPool<Object, String>();
    }

    protected int getPoolSize()
    {
        return randomIntBetween(4, 10);
    }
}
