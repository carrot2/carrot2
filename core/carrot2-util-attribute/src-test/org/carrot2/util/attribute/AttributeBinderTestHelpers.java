package org.carrot2.util.attribute;

/**
 * Test helpers (different accessibility scope).
 */
public class AttributeBinderTestHelpers
{
    private static class PrivateAttribute implements Runnable 
    {
        public void run()
        {
        }
    }

    public static Class<? extends Runnable> getPrivateImplClassRef()
    {
        return PrivateAttribute.class;
    }
}
