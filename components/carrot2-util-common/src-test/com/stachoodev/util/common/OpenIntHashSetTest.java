/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.util.common;

import cern.colt.list.*;
import junit.framework.*;
import junitx.framework.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class OpenIntHashSetTest extends TestCase
{
    /**
     *  
     */
    public void testDistinctValues()
    {
        OpenIntHashSet set = new OpenIntHashSet();
        set.add(1);
        set.add(100);
        set.add(1541);
        IntArrayList values = new IntArrayList(set.size());
        set.values(values);
        values.sort();
        ArrayAssert.assertEquals("Wrong values", new int []
        { 1, 100, 1541 }, values.elements());
    }

    /**
     *  
     */
    public void testDuplicateValues()
    {
        OpenIntHashSet set = new OpenIntHashSet();
        set.add(1);
        set.add(100);
        set.add(1541);
        set.add(1);
        set.add(100);
        set.add(1541);
        IntArrayList values = new IntArrayList(set.size());
        set.values(values);
        values.sort();
        ArrayAssert.assertEquals("Wrong values", new int []
        { 1, 100, 1541 }, values.elements());
    }
    
    /**
     *  
     */
    public void testGrowing()
    {
        OpenIntHashSet set = new OpenIntHashSet(5);
        set.add(1);
        set.add(10);
        set.add(74);
        set.add(100);
        set.add(1541);
        set.add(154100);
        IntArrayList values = new IntArrayList(set.size());
        set.values(values);
        values.sort();
        ArrayAssert.assertEquals("Wrong values", new int []
        { 1, 10, 74, 100, 1541, 154100 }, values.elements());
    }    

    /**
     * 
     */
    public void testEmptyIterator()
    {
        OpenIntHashSet set = new OpenIntHashSet(5);
        IntIterator iterator = set.iterator();
        assertEquals(false, iterator.hasNext());
    }
    
    /**
     * 
     */
    public void testNonEmptyIterator()
    {
        OpenIntHashSet set = new OpenIntHashSet(5);
        set.add(1);
        set.add(10);
        set.add(74);
        set.add(100);
        set.add(1541);
        IntIterator iterator = set.iterator();
        
        assertEquals(true, iterator.hasNext());
        assertEquals(1, iterator.next());
        assertEquals(true, iterator.hasNext());
        assertEquals(74, iterator.next());
        assertEquals(true, iterator.hasNext());
        assertEquals(10, iterator.next());
        assertEquals(true, iterator.hasNext());
        assertEquals(1541, iterator.next());
        assertEquals(true, iterator.hasNext());
        assertEquals(100, iterator.next());
        assertEquals(false, iterator.hasNext());
    }
}