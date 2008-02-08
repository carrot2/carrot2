package org.carrot2.util.resource;

import java.io.*;


/**
 * A local filesystem resource.
 */
public final class FileResource implements Resource
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
        return ResourceUtils.prefetch(new FileInputStream(file));
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
