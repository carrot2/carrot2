
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;


/**
 * A query cached in a filesystem location somewhere
 */
public class XmlFileCachedQueriesContainer
    implements CachedQueriesContainer
{
    private static final Logger log = Logger.getLogger(XmlFileCachedQueriesContainer.class);
    private File dir;
    private long sizeLimit = 5 * 1024 * 1024; // 5mb limit default
    private long currentSize = 0;
    private HashMap cache = new HashMap();
    private LinkedList lru = new LinkedList();
    private List listeners = new LinkedList();

    public void configure()
    {
        if (this.dir == null)
        {
            throw new RuntimeException("Filesystem directory for cached files not set.");
        }

        if (this.sizeLimit <= 0)
        {
            throw new RuntimeException("Illegal size limit: " + sizeLimit);
        }
    }


    public Iterator getCachedElementSignatures()
    {
        synchronized (this.cache)
        {
            return new ArrayList(lru).iterator();
        }
    }


    public void setSizeLimit(int limit)
    {
        log.debug("Setting size limit to: " + limit);
        this.sizeLimit = limit;
    }


    public void setUseSystemTemp(boolean flag)
    {
        String tempDir = System.getProperty("java.io.tmpdir");

        if (tempDir == null)
        {
            throw new RuntimeException(
                "Cannot use temporary directory (no System java.io.tmpdir property)"
            );
        }

        File temp = new File(tempDir);

        if (!temp.isDirectory() || !temp.canWrite())
        {
            throw new RuntimeException(
                "Cannot use temporary directory " + temp.getAbsolutePath()
                + " - not a dir or cannot write"
            );
        }

        dir = new File(temp, "carrot2");

        if (!dir.exists())
        {
            dir.mkdir();
        }

        if (dir.isDirectory())
        {
            setAbsoluteDir(dir);
        }
        else
        {
            throw new RuntimeException(
                "Cannot use temporary directory " + temp.getAbsolutePath()
                + " - not a dir or cannot write"
            );
        }

        log.debug("Using system temporary directory.");
    }


    public void setAbsoluteDir(File dir)
    {
        if ((dir == null) || (dir.isDirectory() == false))
        {
            throw new IllegalArgumentException("Directory must exist.");
        }

        log.debug("Setting directory to: " + dir.getAbsolutePath());

        this.dir = dir;

        // read existing files and add them to cache.
        File [] files = dir.listFiles();
        Arrays.sort(
            files,
            new Comparator()
            {
                public int compare(Object a, Object b)
                {
                    long tmp = ((File) a).lastModified() - ((File) b).lastModified();

                    if (tmp < 0)
                    {
                        return -1;
                    }
                    else if (tmp > 0)
                    {
                        return 1;
                    }
                    else
                    {
                        return 0;
                    }
                }
            }
        );

        for (int i = 0; i < files.length; i++)
        {
            try
            {
                XmlFileCachedQuery q = new XmlFileCachedQuery(files[i]);
                this.cache.put(q.getSignature(), q);
                this.lru.addLast(q.getSignature());
                this.currentSize += files[i].length();
            }
            catch (IOException e)
            {
                log.error(
                    "Cannot read cached object '" + files[i].getName() + "': " + e.toString()
                );
            }
        }
    }


    public void expungeFromCache(Object signature)
    {
        synchronized (this.cache)
        {
            XmlFileCachedQuery cq;

            if ((cq = (XmlFileCachedQuery) cache.remove(signature)) != null)
            {
                this.lru.remove(signature);
                this.currentSize -= cq.getFile().length();

                if (cq.getFile().delete() == false)
                {
                    log.error("Cannot physically remove file: " + cq.getFile().getAbsolutePath());
                }
            }
        }
    }


    public CachedQuery addToCache(CachedQuery q)
    {
        synchronized (this.cache)
        {
            CachedQuery cq;

            if ((cq = (CachedQuery) cache.get(q.getSignature())) != null)
            {
                return cq;
            }

            File fname = generateUniqueName();

            try
            {
                XmlFileCachedQuery.createEntry(fname, q);
                cq = new XmlFileCachedQuery(fname);

                this.cache.put(cq.getSignature(), cq);
                this.lru.addFirst(cq.getSignature());
                this.currentSize += fname.length();

                checkLimits();
            }
            catch (IOException e)
            {
                log.error("Cannot create cached query: " + e.toString());

                return null;
            }

            return cq;
        }
    }


    private void checkLimits()
    {
        while ((this.currentSize > this.sizeLimit) && (this.cache.size() > 0))
        {
            // pick the victim amd remove it.
            Object signature = lru.getLast();
            CachedQuery cq = this.getCachedElement(signature);

            for (Iterator i = this.listeners.iterator(); i.hasNext();)
            {
                ((CacheListener) i.next()).elementRemovedFromCache(cq);
            }

            this.expungeFromCache(signature);
        }
    }

    private final Random filenameGenerator = new Random(System.currentTimeMillis());

    private File generateUniqueName()
    {
        while (true)
        {
            long next = filenameGenerator.nextLong();
            File f = new File(dir, Long.toHexString(next));

            try
            {
                if (f.createNewFile() == false)
                {
                    // restart random number generator so that cycles
                    // are impossible
                    filenameGenerator.setSeed(System.currentTimeMillis());

                    continue;
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException("Cannot create temp. file for query cache name.");
            }

            return f;
        }
    }


    public CachedQuery getCachedElement(Object signature)
    {
        synchronized (this.cache)
        {
            return (CachedQuery) cache.get(signature);
        }
    }


    public Iterator getCacheListeners()
    {
        synchronized (this.cache)
        {
            return this.listeners.iterator();
        }
    }


    public void addCacheListener(CacheListener l)
    {
        synchronized (this.cache)
        {
            listeners.add(l);
        }
    }


    public void removeCacheListener(CacheListener l)
    {
        synchronized (this.cache)
        {
            listeners.remove(l);
        }
    }
}
