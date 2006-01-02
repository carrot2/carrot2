
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
package com.dawidweiss.carrot.remote.controller.process;


import java.io.*;


public class ProcessDescriptorFileAnchor
    implements ProcessDescriptorAnchor
{
    private final File pd;
    private long lastModified;

    public ProcessDescriptorFileAnchor(File file)
    {
        this.pd = file;

        if (!file.canRead())
        {
            throw new IllegalArgumentException("Cannot read process: " + file.getAbsolutePath());
        }

        this.lastModified = pd.lastModified();
    }

    public boolean isUpToDate()
    {
        return lastModified >= pd.lastModified();
    }


    public InputStream openStream()
    {
        try
        {
            InputStream is = new FileInputStream(pd);
            this.lastModified = pd.lastModified();

            return is;
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
    }
}
