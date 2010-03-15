
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

package org.carrot2.core;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test cases for {@link Document}.
 */
public class DocumentTest
{
    @Test()
    public void testNoIdentifiers()
    {
        final Document d1 = new Document();
        final Document d2 = new Document();
        final Document d3 = new Document();

        Document.assignDocumentIds(Lists.newArrayList(d1, d2, d3));
        assertThat(d1.id).isEqualTo(0);
        assertThat(d2.id).isEqualTo(1);
        assertThat(d3.id).isEqualTo(2);
    }

    @Test()
    public void testSomeIdentifiers()
    {
        final Document d1 = new Document();
        d1.id = 2;
        final Document d2 = new Document();
        final Document d3 = new Document();
        final Document d4 = new Document();
        d4.id = 5;
        final Document d5 = new Document();
        
        Document.assignDocumentIds(Lists.newArrayList(d1, d2, d3, d4, d5));
        assertThat(d1.id).isEqualTo(2);
        assertThat(d2.id).isEqualTo(6);
        assertThat(d3.id).isEqualTo(7);
        assertThat(d4.id).isEqualTo(5);
        assertThat(d5.id).isEqualTo(8);
    }
    
    @Test
    public void testNonUniqueIdentifiers()
    {
        final Document d1 = new Document();
        d1.id = 0;
        final Document d2 = new Document();
        d2.id = 0;

        Document.assignDocumentIds(Lists.newArrayList(d1, d2));
        assertThat(d1.id).isEqualTo(0);
        assertThat(d2.id).isEqualTo(1);
    }
}
