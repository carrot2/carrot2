
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.pool;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link FixedSizePool}.
 */
public class FixedSizePoolTest
{
    private FixedSizePool<Object, String> pool;
    private int poolSize = 4;

    @Before
    public void initializePool()
    {
        pool = new FixedSizePool<Object, String>(poolSize);
    }

    @After
    public void disposePool()
    {
        if (pool != null) pool.dispose();
    }
    
    @Test
    public void testStress() throws Exception
    {
        final String [] params = {"a", "b", "c"};

        class Worker extends Thread
        {
            public volatile Throwable t;

            public void run()
            {
                try
                {
                    for (int i = 0; i < 50; i++)
                    {
                        Object o = pool.borrowObject(Object.class, params[i % params.length]);
                        Thread.sleep(10);
                        pool.returnObject(o, params[i % params.length]);
                    }
                }
                catch (Throwable e)
                {
                    this.t = e;
                }
            }
        };

        Worker [] threads = new Worker [poolSize * 5];
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new Worker();
            threads[i].start();
        }

        for (Worker w : threads)
        {
            w.join();
            if (w.t != null) 
                throw new RuntimeException("Worker failed, see nested.", w.t);
        }
    }

    @Test
    public void testExhaustAndReleaseOnDispose() throws Exception
    {
        final Thread t1 = Thread.currentThread();
        final Thread t2 = new Thread() {
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
            for (int i = 0; i < poolSize + 1; i++)
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

    @Test
    public void testBorrowReturnYieldsSameObject() throws Exception
    {
        final String newString = pool.borrowObject(String.class, null);
        pool.returnObject(newString, null);

        final String recycledString = pool.borrowObject(String.class, null);
        assertSame(newString, recycledString);
    }

    @Test
    public void testBorrowBorrowYieldsDifferentObjects() throws Exception
    {
        final String newStringA = pool.borrowObject(String.class, null);
        final String newStringB = pool.borrowObject(String.class, null);

        assertNotSame(newStringA, newStringB);

        pool.returnObject(newStringA, null);
        final String recycledString = pool.borrowObject(String.class, null);
        assertSame(newStringA, recycledString);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalStateOnBorrowAfterDispose() throws Exception
    {
        final String newString = pool.borrowObject(String.class, null);
        pool.dispose();
        pool.returnObject(newString, null); // Can return the object, no worries.
        pool.borrowObject(String.class, null); // Causes IllegalStateException
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testParametersAndFlow() throws InstantiationException, IllegalAccessException
    {
        IMocksControl mocksControl = createStrictControl();

        final IInstantiationListener<String, String> instantiationListener = 
            mocksControl.createMock(IInstantiationListener.class);
        final IActivationListener<String, String> activationListener = 
            mocksControl.createMock(IActivationListener.class);
        final IPassivationListener<String, String> passivationListener = 
            mocksControl.createMock(IPassivationListener.class);
        final IDisposalListener<String, String> disposalListener = 
            mocksControl.createMock(IDisposalListener.class);

        instantiationListener.objectInstantiated(isA(String.class), eq("p1"));
        instantiationListener.objectInstantiated(isA(String.class), eq("p1"));
        activationListener.activate((String) anyObject(), eq("p1"));
        passivationListener.passivate((String) anyObject(), eq("p1"));
        instantiationListener.objectInstantiated(isA(String.class), (String) isNull());
        instantiationListener.objectInstantiated(isA(String.class), (String) isNull());
        activationListener.activate((String) anyObject(), (String) isNull());
        passivationListener.passivate((String) anyObject(), (String) isNull());
        mocksControl.checkOrder(false);
        disposalListener.dispose((String) anyObject(), (String) isNull());
        disposalListener.dispose((String) anyObject(), (String) isNull());
        disposalListener.dispose((String) anyObject(), eq("p1"));
        disposalListener.dispose((String) anyObject(), eq("p1"));
        mocksControl.replay();

        FixedSizePool<String, String> pool = new FixedSizePool<String, String>(2);
        pool.init(instantiationListener, activationListener, 
            passivationListener, disposalListener);

        final String objectP1 = pool.borrowObject(String.class, "p1");
        pool.returnObject(objectP1, "p1");

        final String objectNull = pool.borrowObject(String.class, null);
        pool.returnObject(objectNull, null);

        assertNotSame(objectP1, objectNull);
        pool.dispose();

        mocksControl.verify();
    }
}
