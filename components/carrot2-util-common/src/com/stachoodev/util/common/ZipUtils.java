
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.util.common;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * A number of useful methods for working with ZIP files.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ZipUtils
{
    /**
     * Adds a new entry to an existing Zip file. Important: A LOT of things can
     * go wrong during that operation, so don't use this method for important
     * data.
     * 
     * @param file
     * @param entry
     * @return 
     * @throws IOException
     */
    public static ZipOutputStream append(File file, ZipEntry newEntry)
        throws IOException
    {
        File parent = file.getParentFile();

        // Rename the original file
        Random random = new Random();
        File renamedFile;
        int trials = 10;
        boolean renamed;
        do
        {
            renamedFile = new File(parent, file.getName() + "."
                + Integer.toString(random.nextInt(1 << 30)));
        }
        while (!(renamed = file.renameTo(renamedFile)) && trials-- > 0);
        if (!renamed)
        {
            return null;
        }

        // Copy the contents to the new file
        byte [] buffer = new byte [4096];
        int bytesRead;
        ZipFile zipFile;
        ZipOutputStream output = null;

        try
        {
            zipFile = new ZipFile(renamedFile);
            output = new ZipOutputStream(new FileOutputStream(file));
            output.setLevel(Deflater.BEST_COMPRESSION);
            for (Enumeration entries = zipFile.entries(); entries
                .hasMoreElements();)
            {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                InputStream input = zipFile.getInputStream(entry);
                output.putNextEntry(entry);
                while ((bytesRead = input.read(buffer)) >= 0)
                {
                    output.write(buffer, 0, bytesRead);
                }
                input.close();
            }
            zipFile.close();
            output.flush();
            output.putNextEntry(newEntry);
        }
        catch (Exception e)
        {
            // If _anything_ goes wrong - clean up and give up
            if (output != null)
            {
                output.close();
            }
            file.delete();
            renamedFile.renameTo(file);
            return null;
        }

        renamedFile.delete();

        return output;
    }
}