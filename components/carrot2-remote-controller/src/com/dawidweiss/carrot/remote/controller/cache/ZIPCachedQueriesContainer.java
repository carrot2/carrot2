
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.remote.controller.cache;


import java.io.File;


/**
 * Container storing cached queries in a dedicated folder and maintaining its size using a soft
 * size limit (the folder may be larger occassionaly, but in the long term it will keep the
 * desired size).
 */
public class ZIPCachedQueriesContainer
    extends AbstractFilesystemCachedQueriesContainer
{
    protected final AbstractFileCachedQuery createNewInstance(File file, CachedQuery q)
        throws java.io.IOException
    {
        return new ZIPCachedQuery(file, q);
    }


    protected final AbstractFileCachedQuery loadInstance(File file)
        throws java.io.IOException
    {
        return new ZIPCachedQuery(file);
    }


    protected final String getTempSubdirName()
    {
        return "carrot2-cache-zips";
    }
}
