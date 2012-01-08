
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

/**
 * Test helpers (different accessibility scope).
 */
public class AttributeBinderTestHelpers
{
    public static class RunnableAdapter implements Runnable
    {
        public void run()
        {
            // Nothing.
        }
    }

    private static class PrivateStaticClass extends RunnableAdapter 
    {
    }

    public class PrivateNestedClass extends RunnableAdapter 
    {
    }

    public static class PublicNoConstructorClass extends RunnableAdapter 
    {
        private PublicNoConstructorClass()
        {
            // Hide the constructor.
        }
    }

    public static class ExceptionInConstructor extends RunnableAdapter 
    {
        public ExceptionInConstructor()
        {
            throw new Error("(original exception)");
        }
    }
    
    public static Class<? extends Runnable> getPrivateStaticClassRef()
    {
        return PrivateStaticClass.class;
    }

    public static Class<? extends Runnable> getPrivateNestedClassRef()
    {
        return PrivateNestedClass.class;
    }
    
    public static Class<? extends Runnable> getPublicNoConstructor()
    {
        return PublicNoConstructorClass.class;
    }

    public static Class<? extends Runnable> getExceptionInConstructor()
    {
        return ExceptionInConstructor.class;
    }        
}
