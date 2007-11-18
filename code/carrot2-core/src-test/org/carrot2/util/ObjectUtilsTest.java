/**
 * 
 */
package org.carrot2.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *
 */
public class ObjectUtilsTest
{
    @Test
    public void testNullNull()
    {
        assertTrue(ObjectUtils.equals(null, null));
    }
    
    public void testNullNotNull()
    {
        assertFalse(ObjectUtils.equals("test", null));
        assertFalse(ObjectUtils.equals(null, "test"));
    }
    
    public void testNotNullNotNull()
    {
        assertTrue(ObjectUtils.equals("test", "test"));
        assertFalse(ObjectUtils.equals("test2", "test"));
    }
}
