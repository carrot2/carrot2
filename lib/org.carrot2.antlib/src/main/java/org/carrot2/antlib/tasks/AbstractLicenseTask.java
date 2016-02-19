
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

package org.carrot2.antlib.tasks;

import java.io.*;
import java.util.LinkedList;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;

/**
 * A common superclass for ANT task for adding, replacing or listing licensing info in
 * source code headers or footers.
 */
abstract class AbstractLicenseTask extends MatchingTask
{
    /**
     * A list of <code>FileSet</code> indicating files with <code>*.java</code>
     * suffix.
     */
    private LinkedList<FileSet> filesets = new LinkedList<FileSet>();

    /**
     * Source file encoding (must be given in an explicit way).
     */
    private String encoding;

    /**
     * If set to <code>true</code>, the task will accept files not ending with
     * <code>.java</code>.
     */
    private boolean forceAllFiles;

    /**
     * Crates a new path with a set of dependency files to scan.
     */
    public FileSet createFileset()
    {
        final FileSet fileset = new FileSet();
        filesets.add(fileset);
        return fileset;
    }

    /**
     * Checks if all parameters are correct.
     * 
     * @throws BuildException
     */
    protected void checkParameters() throws BuildException
    {
        if (getProject() == null)
        {
            throw new BuildException("Project reference is required.");
        }

        if (getEncoding() == null)
        {
            throw new BuildException(
                "Source file encoding is required (an explicit attribute)");
        }
        else
        {
            try
            {
                /*
                 * We log the message and at the same time check if the encoding is all
                 * right.
                 */
                super.log("Source files encoding is: "
                    + new String(getEncoding().getBytes(getEncoding()), getEncoding()),
                    Project.MSG_VERBOSE);
            }
            catch (UnsupportedEncodingException e)
            {
                throw new BuildException("Encoding not supported: " + getEncoding());
            }
        }
    }

    /**
     * Executes the task.
     */
    public void execute() throws BuildException
    {
        checkParameters();

        final FileUtils fUtils = FileUtils.getFileUtils();
        final Project project = getProject();
        for (FileSet fileset : filesets)
        {
            final DirectoryScanner scanner = fileset.getDirectoryScanner(project);

            final File fromDir = fileset.getDir(project);
            final String [] srcFiles = scanner.getIncludedFiles();

            for (int fIndex = 0; fIndex < srcFiles.length; fIndex++)
            {
                final File file = fUtils.resolveFile(fromDir, srcFiles[fIndex]);

                if (!file.isFile())
                {
                    continue;
                }

                if (false == forceAllFiles && false == file.getName().endsWith(".java"))
                {
                    throw new BuildException(
                        "File does not end with '.java'. Use 'force' attribute to override.");
                }

                try
                {
                    checkLicense(file);
                }
                catch (IOException e)
                {
                    throw new BuildException("Could not process file: "
                        + file.getAbsolutePath(), e);
                }
            }
        }
    }

    /**
     * Invoked for each file where licensing should be checked.
     */
    protected abstract void checkLicense(File file) throws IOException, BuildException;

    /**
     * Set source file encoding.
     */
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    /**
     * Set to <code>true</code> to make the task accept files not ending with the
     * default Java source file suffix.
     */
    public void setForce(boolean force)
    {
        this.forceAllFiles = force;
    }

    /**
     * Returns current source file encoding or <code>null</code> if none yet.
     */
    protected String getEncoding()
    {
        return encoding;
    }

    /**
     * Reads a single file into memory.
     */
    protected final String readFile(File file) throws IOException
    {
        final byte [] content = new byte [(int) file.length()];
        final FileInputStream fis = new FileInputStream(file);
        try
        {
            if (fis.read(content) != content.length)
            {
                throw new IOException("Could not read all bytes from file: "
                    + file.getAbsolutePath());
            }
            return new String(content, getEncoding());
        }
        finally
        {
            try
            {
                fis.close();
            }
            catch (IOException e)
            {
                // Ignore exception.
            }
        }
    }

    /**
     * Write a string to file. The encoding is acquired from {@link #getEncoding()}.
     */
    protected void writeFile(String string, File file) throws IOException
    {
        final byte [] content = string.getBytes(getEncoding());
        final FileOutputStream fos = new FileOutputStream(file);
        try
        {
            fos.write(content);
        }
        finally
        {
            try
            {
                fos.close();
            }
            catch (IOException e)
            {
                // Ignore exception.
            }
        }
    }
}
