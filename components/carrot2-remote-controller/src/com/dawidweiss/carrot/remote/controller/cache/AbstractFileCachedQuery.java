

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.remote.controller.cache;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query;


/**
 * A query cached in a filesystem location somewhere. This is an abstract superclass. Subclasses
 * will provide methods of query serialization to file.
 */
abstract class AbstractFileCachedQuery
    extends CachedQuery
{
    protected File file;
    protected Query query;
    protected String componentId;
    protected Map optionalParams;

    /**
     * Attempts to read the cached query from disk.
     */
    public AbstractFileCachedQuery(File cacheFile)
        throws IOException
    {
        if (!cacheFile.exists() || !cacheFile.canRead())
        {
            throw new IOException("Cannot read cached query file: " + cacheFile.getAbsolutePath());
        }

        this.file = cacheFile;
        loadDataFromFile();
    }


    /**
     * Attempts to create a copy of some other cachedquery on disk at the specified location.
     */
    public AbstractFileCachedQuery(File cacheFile, CachedQuery q)
        throws IOException
    {
        this.file = cacheFile;

        // ok, we have been first - we can create the cached query
        try
        {
            this.query = q.getQuery();
            this.optionalParams = q.getOptionalParams();
            this.componentId = q.getComponentId();
            dumpDataToFile(q.getData());
        }
        catch (IOException e)
        {
            // can we delete the file? What about read-only caches?
            // delete();
            throw e;
        }
        catch (RuntimeException e)
        {
            // can we delete the file? What about read-only caches? delete();
            // delete();
            throw e;
        }
    }

    public final Query getQuery()
    {
        return query;
    }


    public final String getComponentId()
    {
        return componentId;
    }


    public final Map getOptionalParams()
    {
        return optionalParams;
    }


    public abstract InputStream getData()
        throws IOException;


    protected abstract void dumpDataToFile(InputStream dataStream)
        throws IOException;


    protected abstract void loadDataFromFile()
        throws IOException;


    final void delete()
    {
        // todo: sweeper thread in case file cannot be deleted (other threads are
        // still accessing it)
        if (this.file.delete() == false)
        {
            getSweeper().sweepLater(file);
        }
    }


    long getStorageSize()
    {
        return file.length();
    }


    protected static FileSweeper getSweeper()
    {
        synchronized (AbstractFileCachedQuery.class)
        {
            if (sweeper == null)
            {
                sweeper = new FileSweeper(1000 * 3);
                sweeper.start();

                return sweeper;
            }
            else
            {
                return sweeper;
            }
        }
    }

    private static FileSweeper sweeper;

    private static class FileSweeper
        extends Thread
    {
        private final Logger log = Logger.getLogger(FileSweeper.class);
        private List toBeRemoved = new LinkedList();
        private final int sweepingPeriodMillis;

        public FileSweeper(int sweepingPeriodMillis)
        {
            super("Cache File Sweeper");
            this.setDaemon(true);
            this.sweepingPeriodMillis = sweepingPeriodMillis;
        }

        public void sweepLater(File f)
        {
            synchronized (toBeRemoved)
            {
                toBeRemoved.add(f);
            }
        }


        public void run()
        {
            while (true)
            {
                // sleep for some time
                try
                {
                    sleep(sweepingPeriodMillis);

                    List l;

                    synchronized (toBeRemoved)
                    {
                        l = new ArrayList(toBeRemoved);
                    }

                    for (Iterator i = l.iterator(); i.hasNext();)
                    {
                        File f = (File) i.next();

                        if (f.delete() == true)
                        {
                            synchronized (toBeRemoved)
                            {
                                toBeRemoved.remove(f);
                            }

                            log.debug("Swept successfully: " + f.getAbsolutePath());
                        }
                        else
                        {
                            log.warn("Sweeping failed: " + f.getAbsolutePath());
                        }
                    }
                }
                catch (InterruptedException e)
                {
                }
            }
        }
    }
}
