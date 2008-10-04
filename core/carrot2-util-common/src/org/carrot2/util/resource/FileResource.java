package org.carrot2.util.resource;

import java.io.*;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.load.Commit;

/**
 * A local filesystem resource.
 */
@Root(name = "file-resource")
public final class FileResource implements Resource
{
    /**
     * File pointed to by this resource.
     */
    private File file;

    /**
     * Absolute path, for serialization only.
     */
    @Attribute(name = "absolute-path")
    private String info;

    FileResource()
    {
    }
    
    public FileResource(File file)
    {
        this.file = file;
        this.info = file.getAbsolutePath();
    }

    public InputStream open() throws IOException
    {
        return ResourceUtils.prefetch(new FileInputStream(file));
    }


    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof FileResource)
        {
            return ((FileResource) obj).file.equals(this.file);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.file.hashCode();
    }

    @Override
    public String toString()
   {
        return info;
    }

    @Commit
    void afterDeserialization()
    {
        file = new File(info);
    }

    public File getFile()
    {
        return file;
    }
}
