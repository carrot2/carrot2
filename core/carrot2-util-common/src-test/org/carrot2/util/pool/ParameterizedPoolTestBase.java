
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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;

import java.util.concurrent.CountDownLatch;

import org.carrot2.util.tests.CarrotTestCase;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.carrotsearch.randomizedtesting.Rethrow;

/**
 * Test cases for {@link FixedSizePool}.
 */
public abstract class ParameterizedPoolTestBase extends CarrotTestCase
{
    protected IParameterizedPool<Object, String> pool;

    protected abstract IParameterizedPool<Object, String> createPool();

    protected abstract int getPoolSize();

    @Before
    public void initializePool()
    {
        pool = createPool();
    }

    @After
    public void disposePool()
    {
        if (pool != null) pool.dispose();
    }

    @Test
    public void testStress() throws Exception
    {
        final String [] params =
        {
            "a", "b", "c"
        };

        final CountDownLatch latch = new CountDownLatch(1);
        class Worker extends Thread
        {
            public void run()
            {
                try {
                    latch.await();
                    for (int i = iterations(25, 100); --i >= 0;)
                    {
                        Object o = pool.borrowObject(Object.class, params[i % params.length]);
                        Thread.sleep(10);
                        pool.returnObject(o, params[i % params.length]);
                    }
                } catch (Exception e) {
                    Rethrow.rethrow(e);
                }
            }
        }

        Worker [] threads = new Worker [getPoolSize() * 5];
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new Worker();
            threads[i].start();
        }
        latch.countDown();

        for (Worker w : threads)
        {
            w.join();
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

    @Test(expected = IllegalStateException.class)
    public void testReturnAlreadyReturnedObject() throws InstantiationException,
        IllegalAccessException
    {
        final String newString = pool.borrowObject(String.class, null);
        pool.returnObject(newString, null);
        pool.returnObject(newString, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testParametersAndFlow() throws InstantiationException,
        IllegalAccessException
    {
        IMocksControl mocksControl = createStrictControl();

        final IInstantiationListener<String, String> instantiationListener = mocksControl
            .createMock(IInstantiationListener.class);
        final IActivationListener<String, String> activationListener = mocksControl
            .createMock(IActivationListener.class);
        final IPassivationListener<String, String> passivationListener = mocksControl
            .createMock(IPassivationListener.class);
        final IDisposalListener<String, String> disposalListener = mocksControl
            .createMock(IDisposalListener.class);

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
        pool.init(instantiationListener, activationListener, passivationListener,
            disposalListener);

        final String objectP1 = pool.borrowObject(String.class, "p1");
        pool.returnObject(objectP1, "p1");

        final String objectNull = pool.borrowObject(String.class, null);
        pool.returnObject(objectNull, null);

        assertNotSame(objectP1, objectNull);
        pool.dispose();

        mocksControl.verify();
    }
}
