
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.odp;

import java.io.*;
import java.util.*;

import junit.framework.*;
import junitx.framework.*;

import org.carrot2.input.odp.index.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class PathTopicIndexTest extends TestCase
{
    /**
     *  
     */
    public void testNoEntries()
    {
        PathTopicIndex index = new PathTopicIndex();

        assertEquals("No Ids found", 0, iteratorToList(index.getIds("a/b")).size());
    }

    /**
     *  
     */
    public void testNonRecursiveBeginning()
    {
        PathTopicIndex index = new PathTopicIndex();
        index.add("a", 0);
        index.add("a/b", 1);
        index.add("a/c", 2);
        index.add("a/c/d", 3);
        index.add("a/c/e", 4);
        index.add("a/c/f", 5);
        index.add("a/e", 6);

        List expectedIds = arrayToList(new int []
        { 0 });

        ListAssert.assertEquals("Correct Ids", expectedIds,
            iteratorToList(index.getIds("a")));
        ListAssert.assertEquals("Correct Ids", expectedIds,
            iteratorToList(index.getIds("a/")));
    }

    /**
     *  
     */
    public void testNonRecursiveMiddle()
    {
        PathTopicIndex index = new PathTopicIndex();
        index.add("t", 9);
        index.add("t/a", 0);
        index.add("t/a/b", 1);
        index.add("t/a/c", 2);
        index.add("t/b", 3);
        index.add("t/b/a", 4);
        index.add("t/b/b", 5);
        index.add("t/b/b/c", 6);
        index.add("t/c", 7);
        index.add("t/c/a", 8);

        List expectedIds = arrayToList(new int  []
        { 5 });

        ListAssert.assertEquals("Correct Ids", expectedIds,
            iteratorToList(index.getIds("t/b/b")));
        ListAssert.assertEquals("Correct Ids", expectedIds,
            iteratorToList(index.getIds("t/b/b/")));
    }

    /**
     *  
     */
    public void testNonRecursiveEnd()
    {
        PathTopicIndex index = new PathTopicIndex();
        index.add("t/a", 0);
        index.add("t/a/b", 1);
        index.add("t/a/c", 2);
        index.add("t/a/c/d", 3);
        index.add("t/a/c/e", 4);
        index.add("t/a/c/f", 5);
        index.add("t/d/d/f", 6);

        List expectedIds = arrayToList(new int []
        { 6 });

        ListAssert.assertEquals("Correct Ids", expectedIds,
            iteratorToList(index.getIds("t/d/d/f")));
        ListAssert.assertEquals("Correct Ids", expectedIds,
            iteratorToList(index.getIds("t/d/d/f/")));
    }

    /**
     *  
     */
    public void testRecursiveBeginning()
    {
        PathTopicIndex index = new PathTopicIndex();
        index.add("t/a", 0);
        index.add("t/a/b", 1);
        index.add("t/a/c", 2);
        index.add("t/a/c/d", 3);
        index.add("t/a/c/e", 4);
        index.add("t/a/c/f", 5);
        index.add("t/b", 6);

        List expectedIds = arrayToList(new int []
        { 0, 1, 2, 3, 4, 5 });

        ListAssert.assertEquals("Correct Ids", expectedIds,
            iteratorToList(index.getIds("t/a/*")));
    }

    /**
     *  
     */
    public void testRecursiveMiddle()
    {
        PathTopicIndex index = new PathTopicIndex();
        index.add("t/a", 0);
        index.add("t/a/b", 1);
        index.add("t/a/c", 2);
        index.add("t/b", 3);
        index.add("t/b/a", 4);
        index.add("t/b/b", 5);
        index.add("t/b/b/c", 6);
        index.add("t/c", 7);
        index.add("t/c/a", 8);

        List expectedIds = arrayToList(new int []
        { 5, 6 });

        ListAssert.assertEquals("Correct Ids", expectedIds,
            iteratorToList(index.getIds("t/b/b/*")));
    }

    /**
     *  
     */
    public void testSingleRecursiveMiddle()
    {
        PathTopicIndex index = new PathTopicIndex();
        index.add("t/a", 0);
        index.add("t/a/b", 1);
        index.add("t/a/c", 2);
        index.add("t/b", 3);
        index.add("t/b/a", 4);
        index.add("t/b/b", 5);
        index.add("t/b/b/c", 6);
        index.add("t/c", 7);
        index.add("t/c/a", 8);

        List expectedIds = arrayToList(new int []
        { 6 });

        ListAssert.assertEquals("Correct Ids", expectedIds,
            iteratorToList(index.getIds("t/b/b/c/*")));
    }

    /**
     *  
     */
    public void testSingleRecursiveSimpleNoTop()
    {
        PathTopicIndex index = new PathTopicIndex();
        index.add("t/a/a", 0);
        index.add("t/b/b", 1);

        List expectedIds = arrayToList(new int []
        { 0, 1 });

        List Ids = iteratorToList(index.getIds("t/*"));
        ListAssert.assertEquals("Correct Ids", expectedIds, Ids);
    }

    /**
     *  
     */
    public void testSingleRecursiveNoTop()
    {
        PathTopicIndex index = new PathTopicIndex();
        index.add("t/a/a", 0);
        index.add("t/a/b", 1);
        index.add("t/a/c", 2);
        index.add("t/b", 3);
        index.add("t/b/a", 4);
        index.add("t/b/b", 5);
        index.add("t/b/b/c", 6);
        index.add("t/c", 7);
        index.add("t/c/a", 8);

        List expectedIds = arrayToList(new int []
        { 0, 1, 2, 3, 4, 5, 6, 7, 8 });

        List Ids = iteratorToList(index.getIds("t/*"));
        ListAssert.assertEquals("Correct Ids", expectedIds, Ids);
    }

    /**
     *  
     */
    public void testRecursiveEnd()
    {
        PathTopicIndex index = new PathTopicIndex();
        index.add("t/a", 0);
        index.add("t/a/b", 1);
        index.add("t/a/c", 2);
        index.add("t/a/c/d", 3);
        index.add("t/a/c/e", 4);
        index.add("t/a/c/f", 5);
        index.add("t/d/d/f", 6);

        List expectedIds = arrayToList(new int []
        { 6 });

        ListAssert.assertEquals("Correct Ids", expectedIds,
            iteratorToList(index.getIds("t/d/d/f/*")));
    }

    public void testSerialization() throws IOException
    {
        PathTopicIndex index = new PathTopicIndex();
        index.add("t/a/a", 0);
        index.add("t/a/b", 1);
        index.add("t/a/c", 2);
        index.add("t/b", 3);
        index.add("t/b/a", 4);
        index.add("t/b/b", 5);
        index.add("t/b/b/c", 6);
        index.add("t/c", 7);
        index.add("t/c/a", 8);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        index.serialize(output);
        output.close();

        ByteArrayInputStream input = new ByteArrayInputStream(output
            .toByteArray());
        PathTopicIndex deserializedIndex = new PathTopicIndex();
        deserializedIndex.deserialize(input);

        List expectedIds = arrayToList(new int []
        { 0, 1, 2, 3, 4, 5, 6, 7, 8 });

        List Ids = iteratorToList(deserializedIndex.getIds("t/*"));
        ListAssert.assertEquals("Correct Ids", expectedIds, Ids);
    }

    /**
     * @param Ids
     */
    private List iteratorToList(IdIterator iter)
    {
        List list = new ArrayList();
        for (; iter.hasNext();)
        {
            list.add(new Integer(iter.next()));
        }
        return list;
    }
    
    /**
     * @param array
     */
    private List arrayToList(int [] array)
    {
        List list = new ArrayList(array.length);
        
        for (int i = 0; i < array.length; i++)
        {
            list.add(new Integer(array[i]));
        }
        
        return list;
    }
}