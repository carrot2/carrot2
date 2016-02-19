
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

import org.junit.Assert;
import org.junit.Test;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakLingering;


/**
 * Test cases for {@link FixedSizePool}.
 */
public class FixedSizePoolTest extends ParameterizedPoolTestBase
{
    protected IParameterizedPool<Object, String> createPool()
    {
        return new FixedSizePool<Object, String>(getPoolSize());
    }

    protected int getPoolSize()
    {
        return 4;
    }

    @ThreadLeakLingering(linger = 2000)
    @Test
    public void testExhaustAndReleaseOnDispose() throws Exception
    {
        final Thread t1 = Thread.currentThread();
        final Thread t2 = new Thread()
        {
            public void run()
            {
                while (t1.getState() != State.WAITING)
                {
                    yield();
                }

                pool.dispose();
            }
        };
        t2.setDaemon(true);
        t2.start();

        try
        {
            for (int i = 0; i < getPoolSize() + 1; i++)
            {
                pool.borrowObject(String.class, null);
            }
            Assert.fail();
        }
        catch (InstantiationException e)
        {
            // Expected.
        }
        catch (IllegalAccessException e)
        {
            Assert.fail(e.toString());
        }
    }
}
