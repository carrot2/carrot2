
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
