/*
 * TempUtils.java
 * 
 * Created on 2004-06-28
 */
package com.stachoodev.carrot.odp;

import java.io.*;
import java.util.*;

/**
 * A number of useful methods for working with temporary files and directories.
 * 
 * @author stachoo
 */
public class TempUtils
{

    /**
     * Creates a temporary directory with given prefix.
     * 
     * @return
     */
    public static File createTemporaryDirectory(String prefix)
    {
        Random random = new Random();
        File temporaryDirectory;

        do
        {
            temporaryDirectory = new File(System.getProperty("java.io.tmpdir")
                + System.getProperty("file.separator") + prefix
                + random.nextInt(1 << 30));
        }
        while (temporaryDirectory.exists());

        temporaryDirectory.mkdirs();

        return temporaryDirectory;
    }

    /**
     * Deletes a directory along with its contents (i.e. the directory can
     * contain files and other directories).
     * 
     * @param directory
     */
    public static void deleteDirectory(File directory)
    {
        if (directory == null || !directory.exists())
        {
            return;
        }

        File [] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isFile())
            {
                files[i].delete();
            }
            else if (files[i].isDirectory())
            {
                deleteDirectory(files[i]);
            }
        }

        directory.delete();
    }
}