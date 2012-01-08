
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

package org.carrot2.util.attribute.constraint;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;

import org.junit.After;
import org.junit.Before;

/**
 * Base class for tests involving files and directories.
 */
public abstract class FileConstraintTestBase<T extends Annotation> extends
    ConstraintTestBase<T>
{
    protected File existingDirectory;
    protected File existingFile;
    protected File nonExisting;

    @Before
    public void prepareFiles() throws IOException
    {
        existingDirectory = File.createTempFile("carrot2", "temp");
        
        /* 
         * Hope for the best, remove the temporary file and recreate it 
         * as a directory. 
         */
        if (!existingDirectory.delete()) 
        {
            throw new RuntimeException("Failed to delete a directory: "
                + existingDirectory.getAbsolutePath());
        }
        if (!existingDirectory.mkdir())
        {
            throw new RuntimeException("Failed to create a directory: "
                + existingDirectory.getAbsolutePath());
        }
        existingFile = File.createTempFile("tempfile", "tmp", existingDirectory);
        nonExisting = new File(existingDirectory, "nonexisting");
    }

    @After
    public void removeFiles()
    {
        if (!existingFile.delete())
        {
            throw new RuntimeException("Failed to delete: "
                + existingFile.getAbsolutePath());
        }

        if (!existingDirectory.delete())
        {
            throw new RuntimeException("Failed to delete: "
                + existingDirectory.getAbsolutePath());
        }
    }
}
