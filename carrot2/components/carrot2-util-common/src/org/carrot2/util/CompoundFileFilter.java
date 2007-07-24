
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

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
