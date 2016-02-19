
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

package org.carrot2.util;

import java.util.*;

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;
import org.carrot2.shaded.guava.common.collect.Lists;
import com.carrotsearch.hppc.IntArrayList;

/**
 * Test cases for {@link GraphUtils}.
 */
public class GraphUtilsTest extends CarrotTestCase
{
    @Test
    public void testEmpty()
    {
        checkAsserts(0, new int [0] [], false, Lists.<IntArrayList> newArrayList());
    }

    @Test
    public void testTrivialArcs()
    {
        checkAsserts(3, new int [0] [], true, Lists.<IntArrayList> newArrayList());
        checkAsserts(3, new int [0] [], false, Arrays.asList(new IntArrayList []
        {
            IntArrayList.from(0),
            IntArrayList.from(1),
            IntArrayList.from(2),
        }));
    }

    @Test
    public void testOneSubgraph()
    {
        final int vertices = 3;
        final int [][] arcs = new int [] []
        {
            new int []
            {
                0, 1
            }
        };

        checkAsserts(vertices, arcs, true, Arrays.asList(new IntArrayList []
        {
            IntArrayList.from(0, 1),
        }));
        checkAsserts(vertices, arcs, false, Arrays.asList(new IntArrayList []
        {
            IntArrayList.from(0, 1),
            IntArrayList.from(2),
        }));
    }

    @Test
    public void testOneBigSubgraph()
    {
        final int vertices = 5;
        final int [][] arcs = new int [] []
        {
            new int []
            {
                0, 1
            },

            new int []
            {
                3, 4
            },

            new int []
            {
                1, 2
            },

            new int []
            {
                3, 2
            }
        };

        checkAsserts(vertices, arcs, true, Arrays.asList(new IntArrayList []
        {
            IntArrayList.from(0, 1, 2, 3, 4),
        }));
        checkAsserts(vertices, arcs, false, Arrays.asList(new IntArrayList []
        {
            IntArrayList.from(0, 1, 2, 3, 4),
        }));
    }

    @Test
    public void testTwoSubgraphs()
    {
        final int vertices = 5;
        final int [][] arcs = new int [] []
        {
            new int []
            {
                0, 1
            },

            new int []
            {
                3, 4
            },

            new int []
            {
                1, 2
            }
        };

        checkAsserts(vertices, arcs, true, Arrays.asList(new IntArrayList []
        {
            IntArrayList.from(0, 1, 2),
            IntArrayList.from(3, 4),
        }));
        checkAsserts(vertices, arcs, false, Arrays.asList(new IntArrayList []
        {
            IntArrayList.from(0, 1, 2),
            IntArrayList.from(3, 4),
        }));
    }

    private void checkAsserts(int vertexCount, int [][] arcs,
        boolean pruneOneNodeSubgraphs, List<IntArrayList> expected)
    {
        List<IntArrayList> subgraphs = GraphUtils.findCoherentSubgraphs(
            vertexCount, 
            new ArrayArcPredicate(vertexCount, arcs), pruneOneNodeSubgraphs);

        assertThat(subgraphs.size()).isEqualTo(expected.size());
        for (int i = 0; i < subgraphs.size(); i++)
        {
            assertEquals(expected.get(i), subgraphs.get(i));
        }
    }

    private static class ArrayArcPredicate implements GraphUtils.IArcPredicate
    {
        private boolean [][] arcs;

        public ArrayArcPredicate(int vertices, int [][] pairs)
        {
            this.arcs = new boolean [vertices] [vertices];
            for (int i = 0; i < pairs.length; i++)
            {
                arcs[pairs[i][0]][pairs[i][1]] = true;
                arcs[pairs[i][1]][pairs[i][0]] = true;
            }
        }

        public boolean isArcPresent(int vertexA, int vertexB)
        {
            return arcs[vertexA][vertexB];
        }
    }
}
