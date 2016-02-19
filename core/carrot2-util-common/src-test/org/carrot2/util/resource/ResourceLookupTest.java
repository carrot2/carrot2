
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

package org.carrot2.util.resource;

import java.io.File;

import org.carrot2.util.resource.ResourceLookup.Location;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

public class ResourceLookupTest extends CarrotTestCase
{
    @Test
    public void testHashCodeEqualsResourceLookup() 
    {
        checkHashEquals(
            new ResourceLookup(Location.CONTEXT_CLASS_LOADER),
            new ResourceLookup(Location.CONTEXT_CLASS_LOADER));

        checkHashEquals(
            new DirLocator(new File(".")),
            new DirLocator(new File(".")));
    }
    
    @Test
    public void testHashCodeEqualsClassLoaderLocator() 
    {
        checkHashEquals(
            new ClassLoaderLocator(this.getClass().getClassLoader()),
            new ClassLoaderLocator(this.getClass().getClassLoader()));
    }

    @Test
    public void testHashCodeEqualsClassLocator() 
    {
        checkHashEquals(
            new ClassLocator(this.getClass()),
            new ClassLocator(this.getClass()));
    }

    @Test
    public void testHashCodeEqualsContextClassLoaderLocator() 
    {
        checkHashEquals(
            new ContextClassLoaderLocator(),
            new ContextClassLoaderLocator());
    }

    @Test
    public void testHashCodeEqualsDirLocator() 
    {
        checkHashEquals(
            new DirLocator(new File(".")),
            new DirLocator(new File(".")));
    }

    @Test
    public void testHashCodeEqualsPrefixDecorator() 
    {
        checkHashEquals(
            new PrefixDecoratorLocator(new DirLocator(new File(".")), "/prefix/"),
            new PrefixDecoratorLocator(new DirLocator(new File(".")), "/prefix/"));
    }

    static void checkHashEquals(Object o1, Object o2)
    {
        assertNotSame(o1, o2);
        assertEquals(o1, o2);
        assertEquals(o1.hashCode(), o2.hashCode());
    }
}
