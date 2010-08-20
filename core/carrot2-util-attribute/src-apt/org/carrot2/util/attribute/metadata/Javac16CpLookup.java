
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

package org.carrot2.util.attribute.metadata;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;

/**
 * Javac 1.6 class path resource lookup hack.
 */
public class Javac16CpLookup implements IClassPathLookup
{
    private final static String JPE = "com.sun.tools.javac.processing.JavacProcessingEnvironment";
    private final static String CTX = "com.sun.tools.javac.util.Context";

    private JavaFileManager fileManager;

    @Override
    public void init(ProcessingEnvironment e)
        throws Exception
    {
        checkClass(e.getClass(), JPE);
        Field f = e.getClass().getDeclaredField("context");
        f.setAccessible(true);

        Object context = f.get(e);
        checkClass(context.getClass(), CTX);

        Method m = context.getClass().getMethod("get", new Class<?> [] {Class.class});

        fileManager = (JavaFileManager) m.invoke(context, JavaFileManager.class);
    }

    private void checkClass(Class<?> clazz, String expected)
    {
        if (!clazz.getName().equals(expected))
            throw new RuntimeException("Processing environment is not " + expected);        
    }

    @Override
    public InputStream getResourceOrNull(String metadataName)
    {
        try
        {
            FileObject fileForInput = fileManager.getFileForInput(StandardLocation.CLASS_PATH, 
                "", metadataName);

            if (fileForInput == null)
                return null;

            return fileForInput.openInputStream();
        }
        catch (Exception e)
        {
            // Ignore.
            return null;
        }
    }
}
