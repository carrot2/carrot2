
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

import junit.framework.TestCase;

import com.dawidweiss.carrot.util.common.FileUtils;

public class ZIPCachedQueriesContainerTest extends TestCase {
    private ZIPCachedQueriesContainer testCache;
    private ZIPCachedQueriesContainer currentCache;
    private File tmpDir;

    public ZIPCachedQueriesContainerTest(String s) {
        super(s);
    }
    
    public void setUp() {
        final File testCacheDir = new File("cache");
        if (!testCacheDir.isDirectory()) {
            fail("Test cache is not a directory: "
                    + testCacheDir.getAbsolutePath());
        }

        this.testCache = new ZIPCachedQueriesContainer();
        testCache.setAbsoluteDir(testCacheDir.getAbsolutePath());
        testCache.configure();

        this.tmpDir = new File("__tmp");
        if (tmpDir.exists()) {
            try {
                FileUtils.deleteDirectoryRecursively(tmpDir);
            } catch (IOException e) {
                fail("Could not delete temporary folder: " + tmpDir);
            }
        }
        if (tmpDir.mkdir() == false) {
            fail("Could not create temporary folder: " + tmpDir);
        }
        
        this.currentCache = new ZIPCachedQueriesContainer();
        this.currentCache.setAbsoluteDir(tmpDir);
        this.currentCache.setSizeLimit(1024 * 1024);
    }
    
    public void tearDown() {
        if (tmpDir.exists()) {
            try {
                FileUtils.deleteDirectoryRecursively(tmpDir);
            } catch (IOException e) {
                fail("Could not delete temporary folder: " + tmpDir);
            }
        }
    }

    public void testStoringQueries() throws IOException {
        CacheRotator rotator = new CacheRotator();
        rotator.copyAll(testCache, currentCache);
        rotator.containsAll(testCache, currentCache);
    }
    
    public void testClear() throws IOException {
        CacheRotator rotator = new CacheRotator();
        rotator.copyAll(testCache, currentCache);
        currentCache.clear();
        assertTrue(currentCache.getCachedElementSignatures().hasNext() == false);
        assertEquals(0, tmpDir.list().length);
    }
}
