
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

package org.carrot2.util.attribute.constraint;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;

import org.apache.commons.io.FileUtils;
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
        existingDirectory = new File(System.getProperty("java.io.tmpdir"),
            "tmp-test-dir");
        if (!existingDirectory.mkdir())
        {
            throw new RuntimeException("Failed to create a directory: "
                + existingDirectory.getAbsolutePath());
        }
        existingFile = new File(existingDirectory, "file");
        FileUtils.touch(existingFile);
        nonExisting = new File(existingDirectory, "nonexisting");
    }

    @After
    public void removeFiles()
    {
        final boolean fileDeleted = existingFile.delete();
        final boolean directoryDeleted = existingDirectory.delete();
        if (!(fileDeleted && directoryDeleted))
        {
            throw new RuntimeException("Failed to delete: "
                + existingFile.getAbsolutePath() + " or "
                + existingDirectory.getAbsolutePath());
        }
    }
}
