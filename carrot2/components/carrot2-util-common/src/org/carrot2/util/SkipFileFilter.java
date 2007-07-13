package org.carrot2.util;

import java.io.File;
import java.io.FileFilter;

/**
 * Skips filenames containing the given substring.
 */
public final class SkipFileFilter implements FileFilter
{
    private final String substring;

    public SkipFileFilter(String substring)
    {
        this.substring = substring;
    }

    public boolean accept(File f)
    {
        return f.getName().indexOf(substring) < 0;
    }
}
