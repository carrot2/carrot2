
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
package com.dawidweiss.carrot.util.common.pools;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

/**
 */
public class ReusableObjectsPoolTest extends TestCase {

	/**
	 * 
	 */
	public ReusableObjectsPoolTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public ReusableObjectsPoolTest(String arg0) {
		super(arg0);
	}

    private class DummyClass {
        private int i = 10;
        private long t = 20;

        DummyClass() {
        }
    }

    /**
     * Test the pool a couple of times. No null 
     * values should be returned.
     */
    public void testSpeedGainWithDifferentPools() {

        /*
         * Watch out, some heavily hacked code below.
         * It's been changed a 1000 times and I don't
         * even know what it used to do in the first place ;D
         */

        if (System.getProperty("performance.tests") == null) {
            Logger.getLogger(this.getClass()).info("Performance test skipped. Define 'performance.tests'" +
                "property to include it.");
            return;
        }

        final int [] counter = new int [1];

        ReusableObjectsFactory factory = new ReusableObjectsFactory() {
			public void createNewObjects(Object[] objects) {
                for (int i=0;i<objects.length;i++) {
                    objects[i] = new DummyClass();
                    counter[0]++;
                }
			}
        };

        int [] hlinksizes = new int [] {
            1, 100, 1000, 10000
        };

        int [] softlinksizes = new int [] {
            1, 100, 1000, 10000
        };

        final int millis = 3 * 1000;
        final int threads = 10;
        final int blockAllocations = 20000;

        Thread [] threadsTable = new Thread [ threads ];
        final boolean [] stop = new boolean[1];
        final int [] iters = new int [threads];
        
        /*
         * 0 = DummyReusableObjectsPool
         * 1 = SoftReusableObjectsPool
         * 2 = HardReusableObjectsPool
         */
        int classes = 2;
        String clname [] = new String [] {
            "Dummy", "Soft ", "Hard "
        };

        for (int i = 0; i<hlinksizes.length;i++) {
            for (int j=0; j<softlinksizes.length;j++) {
                for (int cl = 0; cl<=classes; cl++) {
                    stop[0] = false;
    
                    counter[0] = 0;
                    java.util.Arrays.fill(iters,0);
    
                    for (int t=0;t<threads;t++) {
                        ReusableObjectsPool poolx = null;
                        switch (cl) {
                            case 0: poolx = new DummyReusableObjectsPool(factory);
                                    break;
                            case 1: poolx = new SoftReusableObjectsPool(factory, hlinksizes[i], softlinksizes[j]);
                                    break;
                            case 2: poolx = new HardReusableObjectsPool(factory, hlinksizes[i], softlinksizes[j]);
                                    break;
                            default:
                                fail("which class is: " + cl);
                        }
                        final ReusableObjectsPool pool = poolx;

                        System.gc();
                        System.runFinalization();
                        
                        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    
                        final int x = t;
                        threadsTable[t] = new Thread() {
                            public void run() {
                                Object [] obs = new Object [ blockAllocations ];
                                while (!stop[0]) {
                                    // allocate objects up to blockAllocations limit.
                                    for (int z=0;z<blockAllocations;z++) {
                                        obs[z] = pool.acquireObject();
                                        iters[x]++;
                                    }
                                    java.util.Arrays.fill(obs, null);
                                    pool.reuse();
                                }
                            }
                        };
                        threadsTable[t].setPriority(Thread.NORM_PRIORITY);
                        threadsTable[t].start();
                    }
    
                    // now wait.
                    synchronized (this) {
                        try {
    						this.wait(millis);
    					} catch (InterruptedException e) {
    					}
                    }
                    
                    // stop threads.
                    stop[0] = true;
                    for (int t=0;t<threads;t++) {
                        try {
    						threadsTable[t].join();
    					} catch (InterruptedException e) {
    					}
                    }
    
                    long avg = 0;
                    for (int h=0;h<threads;h++)
                        avg += iters[h];
                    avg = avg / iters.length;

                    System.out.print(clname[cl] + ": hard=" + hlinksizes[i] + ", increase=" + softlinksizes[j]
                        + ", allocations-made=" + counter[0] + ", avg.-iterations-per-thread: "
                            + avg);

                    //for (int h=0;h<threads;h++)  
                    //    System.out.print(iters[h] + ", ");
                    System.out.println();
                }
            }
        }
    }
}
