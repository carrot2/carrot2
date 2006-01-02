
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

import junit.framework.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ZipUtilsTest extends TestCase
{
    /**
     * @throws IOException
     */
    public void testAppend() throws IOException
    {
        File originalFile = File.createTempFile("zutest", null);
        ZipOutputStream output = new ZipOutputStream(new FileOutputStream(
            originalFile));

        // Put a few entries
        byte [] data01 = new byte []
        { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
        output.putNextEntry(new ZipEntry("entry01"));
        output.write(data01);

        byte [] data02 = new byte []
        { 34, 62, 23, 43 };
        output.putNextEntry(new ZipEntry("entry02"));
        output.write(data02);

        byte [] data03 = new byte []
        { 13, 63, 54, 25, 123 };
        output.putNextEntry(new ZipEntry("entry03"));
        output.write(data03);
        output.close();

        // Add a new entry
        ZipOutputStream appendStream = ZipUtils.append(originalFile,
            new ZipEntry("newEntry"));

        byte [] newData = new byte []
        { 6, 34, 4, 62 };
        appendStream.write(newData);
        appendStream.close();

        // Check the results
        ZipFile zipFile = new ZipFile(originalFile);
        Enumeration entries = zipFile.entries();
        ZipEntry entry;
        InputStream input;

        entry = (ZipEntry) entries.nextElement();
        assertEquals("Entry01 name", "entry01", entry.getName());
        input = zipFile.getInputStream(entry);
        assertTrue("Entry01 content", compareContent(input, data01));

        entry = (ZipEntry) entries.nextElement();
        assertEquals("Entry02 name", "entry02", entry.getName());
        input = zipFile.getInputStream(entry);
        assertTrue("Entry02 content", compareContent(input, data02));

        entry = (ZipEntry) entries.nextElement();
        assertEquals("Entry03 name", "entry03", entry.getName());
        input = zipFile.getInputStream(entry);
        assertTrue("Entry03 content", compareContent(input, data03));

        entry = (ZipEntry) entries.nextElement();
        assertEquals("New entry name", "newEntry", entry.getName());
        input = zipFile.getInputStream(entry);
        assertTrue("New entry content", compareContent(input, newData));

        // Delete the file
        zipFile.close();
        originalFile.delete();
    }

    /**
     * @param input
     * @param data
     * @return @throws IOException
     */
    private boolean compareContent(InputStream input, byte [] data)
        throws IOException
    {
        byte [] buffer = new byte [16];
        int bytesRead;
        int totalBytesRead = 0;
        int dataPos = 0;

        while ((bytesRead = input.read(buffer)) >= 0)
        {
            for (int i = 0; i < bytesRead; i++)
            {
                if (dataPos > data.length || data[dataPos++] != buffer[i])
                {
                    return false;
                }
            }
            totalBytesRead += bytesRead;
        }

        return totalBytesRead == data.length;
    }
}