
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

package org.carrot2.core;

import org.carrot2.util.annotations.AspectModified;

/**
 * Provides the runtime execution platform environment information. 
 * 
 * <p>Carrot2 is cross-compiled (with many adaptations) to .NET using IKVM and this switch
 * permits us omit certain irrelevant tests.</p> 
 */
public enum Platform
{
    JAVA, DOTNET;

    /**
     * Returns the current execution platform (hardcoded at build time). 
     */
    @AspectModified("Platform switch replaced using an aspect.")
    public static Platform getPlatform()
    {
        return JAVA;
    }
}
