
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.resources;

import java.io.*;


/**
 * 
 */
public class FileResource implements Resource
{
    private final File file;
    private final String info;

    public FileResource(File file)
    {
        this.file = file;
        this.info = "[file: " + file.getAbsolutePath() + "]";
    }

    public InputStream open() throws IOException
    {
        return new FileInputStream(file);
    }

    public String toString() {
        return info;
    }
    
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj instanceof FileResource) {
            return ((FileResource) obj).file.equals(this.file);
        }
        return false;
    }

    public int hashCode()
    {
        return this.file.hashCode();
    }    
}
