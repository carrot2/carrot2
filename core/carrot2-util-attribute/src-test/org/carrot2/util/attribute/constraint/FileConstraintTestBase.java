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
        existingDirectory = new File("tmp-test-dir");
        existingDirectory.mkdir();
        existingFile = new File(existingDirectory, "file");
        FileUtils.touch(existingFile);
        nonExisting = new File(existingDirectory, "nonexisting");

        existingFile.deleteOnExit();
        existingDirectory.deleteOnExit();
    }

    @After
    public void removeFiles()
    {
        existingFile.delete();
        existingDirectory.delete();
    }
}
