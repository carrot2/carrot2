package org.carrot2.util;

import java.io.File;
import java.io.FileFilter;

/**
 * A cascade of {@link FileFilter}s applied successively.
 */
public final class CompoundFileFilter implements FileFilter
{
    private final FileFilter [] filters;

    /**
     * 
     */
    public CompoundFileFilter(FileFilter [] filters)
    {
        this.filters = filters;
    }

    /**
     * 
     */
    public boolean accept(File f)
    {
        for (int i = 0; i < filters.length; i++)
        {
            if (!filters[i].accept(f))
            {
                return false;
            }
        }

        return true;
    }
}
