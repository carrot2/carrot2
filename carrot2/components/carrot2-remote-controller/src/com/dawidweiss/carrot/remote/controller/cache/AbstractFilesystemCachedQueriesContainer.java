
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
import java.util.*;

import org.apache.log4j.Logger;
import org.dom4j.Element;


/**
 * Container storing cached queries in a dedicated folder and maintaining its size using a soft
 * size limit (the folder may be larger occassionaly, but in the long term it will keep the
 * desired size).
 */
public abstract class AbstractFilesystemCachedQueriesContainer
    implements CachedQueriesContainer
{
    private static final Logger log = Logger.getLogger(
            AbstractFilesystemCachedQueriesContainer.class
        );
    private File dir;
    private String contextRelative;
    private boolean isContextRelative = false;
    private long sizeLimit = 5 * 1024 * 1024; // 5mb limit default
    private long currentSize = 0;
    private boolean readonly = false;
    private HashMap cache = new HashMap();
    private LinkedList lru = new LinkedList();
    private List listeners = new LinkedList();

    public void configure()
    {
        if (this.dir == null)
        {
            throw new RuntimeException("Filesystem directory for cached files not set.");
        }

        if (isContextRelative)
        {
            throw new RuntimeException(
                "Set servlet context path to resolve the context-relative dir: " + dir
            );
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


    public void setReadOnly(boolean value)
    {
        log.debug("Setting as read only: " + value);
        this.readonly = value;
    }


    public void setContextRelativeDir(String contextPath)
    {
        log.debug("Setting context-relative directory: " + contextPath);
        this.contextRelative = contextPath;
    }


    public void setServletBase(String servletBasePath)
    {
        if (contextRelative != null)
        {
            dir = new File(servletBasePath + contextRelative);
            this.setAbsoluteDir(dir);
            isContextRelative = false;
        }
    }


    public void setUseSystemTemp()
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

        dir = new File(temp, getTempSubdirName());

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


    public void setAbsoluteDir(String path)
    {
        setAbsoluteDir(new File(path));
    }


    protected void setAbsoluteDir(File dir)
    {
        if ((dir == null) || (dir.isDirectory() == false))
        {
            throw new IllegalArgumentException("Directory must exist: " + dir.getAbsolutePath());
        }

        log.debug("Setting directory to: " + dir.getAbsolutePath());

        this.dir = dir;

        // read existing files and add them to cache.
        File [] files = dir.listFiles();
        List l = new ArrayList(files.length);

        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isFile() && files[i].canRead())
            {
                l.add(files[i]);
            }
        }

        files = new File[l.size()];
        l.toArray(files);

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
                AbstractFileCachedQuery q = loadInstance(files[i]);

                this.cache.put(q.getSignature(), q);
                this.lru.addLast(q.getSignature());
                this.currentSize += q.getStorageSize();
            }
            catch (IOException e)
            {
                log.error(
                    "Cannot read cached object '" + files[i].getName() + "': " + e.toString()
                );
            }
        }
    }


    /**
     * Create a new cached query instance for the existing file.
     */
    protected abstract AbstractFileCachedQuery loadInstance(File file)
        throws IOException;


    /**
     * Create a new cache query for the provided data
     */
    protected abstract AbstractFileCachedQuery createNewInstance(File file, CachedQuery q)
        throws IOException;


    /**
     * Returns the name of a folder created in system's temporary folder, if using a temporary
     * directory.
     */
    protected abstract String getTempSubdirName();


    public void expungeFromCache(Object signature)
    {
        if (this.readonly)
        {
            return;
        }

        synchronized (this.cache)
        {
            AbstractFileCachedQuery cq;

            if ((cq = (AbstractFileCachedQuery) cache.remove(signature)) != null)
            {
                this.lru.remove(signature);
                this.currentSize -= cq.getStorageSize();
                cq.delete();
            }
        }
    }


    public CachedQuery addToCache(CachedQuery q)
    {
        if (this.readonly)
        {
            return null;
        }

        synchronized (this.cache)
        {
            AbstractFileCachedQuery cq;

            if ((cq = (AbstractFileCachedQuery) cache.get(q.getSignature())) != null)
            {
                return cq;
            }

            File fname = generateUniqueName();

            try
            {
                cq = this.createNewInstance(fname, q);

                this.cache.put(cq.getSignature(), cq);
                this.lru.addFirst(cq.getSignature());
                this.currentSize += cq.getStorageSize();

                checkLimits();
            }
            catch (IOException e)
            {
                log.error("Cannot create cached query.", e);

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

    public void clear() {
        synchronized (this.cache)
        {
            log.info("Clearing cache.");
            for (Iterator i = getCachedElementSignatures(); i.hasNext();) {
                final Object signature = i.next();
                this.expungeFromCache(signature);
            }
        }
    }
    
    public void setConfiguration(Element container) {
        if (container.element("size-limit") != null) {
            this.sizeLimit = Integer.parseInt(container.element("size-limit").getTextTrim());
        }
        if (container.element("use-system-temp") != null) {
            if (Boolean.valueOf(container.element("use-system-temp").getTextTrim()).booleanValue()) {
                this.setUseSystemTemp();
            }
        }
        if (container.element("read-only") != null) {
            if (Boolean.valueOf(container.element("read-only").getTextTrim()).booleanValue()) {
                this.setReadOnly(true);
            }
        }
        if (container.element("context-relative-dir") != null) {
            final String crd = container.elementText("context-relative-dir");
            this.setContextRelativeDir(crd);
        }
    }
}
