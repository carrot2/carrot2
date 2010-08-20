
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.resource;

import java.io.*;

import org.codehaus.jackson.annotate.*;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

/**
 * A local filesystem resource.
 */
@Root(name = "file-resource")
@JsonAutoDetect(JsonMethod.NONE)
public final class FileResource implements IResource
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

    @JsonIgnore
    public File getFile()
    {
        return file;
    }
    
    @JsonProperty
    @SuppressWarnings("unused")
    private String getAbsolutePath()
    {
        return info;
    }
    
    public static FileResource valueOf(String path)
    {
        // Return non-null value only if the string is a path to some existing file.
        final File file = new File(path);
        if (!file.exists())
        {
            return null;
        }
        
        return new FileResource(file);
    }
}
