package org.carrot2.util.pool;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link SoftUnboundedPool}.
 */
public class SoftUnboundedPoolTest
{
    private SoftUnboundedPool<Object> pool;

    @Before
    public void initializePool()
    {
        pool = new SoftUnboundedPool<Object>();
    }

    @Test
    public void testBorrowOneObject() throws InstantiationException,
        IllegalAccessException
    {
        final String newString = pool.borrowObject(String.class);
        pool.returnObject(newString);

        final String recycledString = pool.borrowObject(String.class);
        assertSame(newString, recycledString);
    }

    @Test
    public void testBorrowMoreObjects() throws InstantiationException,
        IllegalAccessException
    {
        final String newStringA = pool.borrowObject(String.class);
        final String newStringB = pool.borrowObject(String.class);

        assertNotSame(newStringA, newStringB);

        pool.returnObject(newStringA);
        final String recycledString = pool.borrowObject(String.class);
        assertSame(newStringA, recycledString);
    }

    @Test(expected = IllegalStateException.class)
    public void testDispose() throws InstantiationException, IllegalAccessException
    {
        final String newString = pool.borrowObject(String.class);
        pool.returnObject(newString);

        final String recycledString = pool.borrowObject(String.class);
        assertSame(newString, recycledString);
        
        pool.dispose();
        pool.returnObject(recycledString); // still can return, nothing is done anyway
        pool.borrowObject(String.class);
    }
}
