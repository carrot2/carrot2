package org.carrot2.util.pool;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link SoftUnboundedPool}.
 */
public class SoftUnboundedPoolTest
{
    private SoftUnboundedPool<Object, String> pool;

    @Before
    public void initializePool()
    {
        pool = new SoftUnboundedPool<Object, String>();
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

    @Test
    @SuppressWarnings("unchecked")
    public void testParameters() throws InstantiationException, IllegalAccessException
    {
        IMocksControl mocksControl = createStrictControl();

        final InstantiationListener<String, String> instantiationListener = mocksControl
            .createMock(InstantiationListener.class);
        instantiationListener.objectInstantiated(isA(String.class), eq("p1"));
        instantiationListener.objectInstantiated(isA(String.class), (String) isNull());
        mocksControl.replay();

        SoftUnboundedPool<String, String> poolWithListeners = new SoftUnboundedPool<String, String>(
            instantiationListener, null, null, null);

        final String objectP1 = poolWithListeners.borrowObject(String.class, "p1");
        poolWithListeners.returnObject(objectP1, "p1");

        final String objectNull = poolWithListeners.borrowObject(String.class);

        assertNotSame(objectP1, objectNull);
        mocksControl.verify();
    }
}
