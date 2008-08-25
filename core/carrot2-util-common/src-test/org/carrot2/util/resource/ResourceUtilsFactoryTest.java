package org.carrot2.util.resource;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * Tests {@link ResourceUtilsFactory}.
 */
public class ResourceUtilsFactoryTest
{
    /**
     * Test default resource locators (by checking context class loader).
     */
    @Test
    public void defaultClassResourceLocator()
    {
        final ResourceUtils before = ResourceUtilsFactory.getDefaultResourceUtils();
        assertNotNull(before.getFirst(this.getClass().getName().replace('.', '/') 
            + ".class"));
    }

    /**
     * Adding custom resource locator.
     */
    @Test
    public void addCustomResourceLocator()
    {
        final ResourceUtils before = ResourceUtilsFactory.getDefaultResourceUtils();
        assertNotNull(before);

        final AtomicInteger wasUsed = new AtomicInteger();

        ResourceUtilsFactory.addLast(new ResourceLocator() {
            public Resource [] getAll(String resource, Class<?> clazz)
            {
                wasUsed.addAndGet(1);
                return new Resource[0];
            }
        });

        ResourceUtilsFactory.addFirst(new ResourceLocator() {
            public Resource [] getAll(String resource, Class<?> clazz)
            {
                wasUsed.addAndGet(1);
                return new Resource[0];
            }
        });

        final ResourceUtils after = ResourceUtilsFactory.getDefaultResourceUtils();
        assertNotSame(before, after);
        
        after.getFirst("resource");
        assertEquals(2, wasUsed.get());
    }
}
