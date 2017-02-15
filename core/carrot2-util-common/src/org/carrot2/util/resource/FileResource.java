
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

package org.carrot2.util.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.carrot2.util.StreamUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A local filesystem resource. This loader provides cached content of
 * returned resources and closes the underlying stream handle in {@link #open()}.
 */
@Root(name = "file-resource")
@JsonAutoDetect(
    creatorVisibility  = JsonAutoDetect.Visibility.NONE,
    fieldVisibility    = JsonAutoDetect.Visibility.NONE,
    getterVisibility   = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility   = JsonAutoDetect.Visibility.NONE)
public final class FileResource implements IResource
{
    /**
     * File pointed to by this resource.
     */
    private Path file;

    /**
     * Absolute path, for serialization only.
     */
    @Attribute(name = "absolute-path")
    private String info;

    FileResource()
    {
    }

    @Deprecated
    public FileResource(File file)
    {
      this(file.toPath());
    }
    
    public FileResource(Path file)
    {
        this.file = file;
        this.info = file.toAbsolutePath().toString();
    }

    public InputStream open() throws IOException
    {
        return StreamUtils.prefetch(Files.newInputStream(file));
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
        file = Paths.get(info);
    }

    @JsonIgnore
    @Deprecated
    public File getFile()
    {
        return file.toFile();
    }

    public String getFileName()
    {
        Path p = file.getFileName();
        if (p != null) {
          return p.toString();
        } else {
          return file.toString();
        }
    }

    @JsonProperty
    private String getAbsolutePath()
    {
        return info;
    }
    
    public static FileResource valueOf(String path)
    {
        // Return non-null value only if the string is a path to some existing file.
        try {
          Path p = Paths.get(path);
          if (Files.exists(p)) {
            return new FileResource(p);
          } else {
            return null;
          }
        } catch (InvalidPathException e) {
          return null;
        } catch (Throwable e) {
          // CARROT-1162 (IKVM throws unchecked exceptions from Files.* on inaccessible folders.
          LoggerFactory.getLogger(FileResource.class)
            .warn("Could not access path: " + path, e);
          return null;
        }
    }

    public Path getPath() {
      return file;
    }
}
