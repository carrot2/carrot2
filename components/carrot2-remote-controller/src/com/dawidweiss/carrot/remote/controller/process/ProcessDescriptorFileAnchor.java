

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
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
