
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

package org.carrot2.util.collect.primitive;

import org.carrot2.util.collect.primitive.IntQueue;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link IntQueue}.
 */
public class IntQueueTest
{
    public IntQueue list;
    
    @Before
    public void before()
    {
        list = new IntQueue();
    }
    
    @Test
    public void testEmpty()
    {
        assertEquals(0, list.size());
    }

    @Test
    public void testAdd()
    {
        list.push(1);
        assertEquals(1, list.size());
        list.push(2);
        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));

        for (int i = 0; i < 10; i++) list.push(i, i);
        assertEquals(22, list.size());
    }

    @Test
    public void testRemove()
    {
        list.push(1, 2);
        list.pop(2);
        assertEquals(0, list.size());

        list.push(1, 2);
        list.pop();
        list.push(3);
        assertEquals(2, list.size());
        assertEquals(3, list.get(1));
    }
}
