/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */

package com.dawidweiss.carrot.perfchecks;

import org.apache.commons.pool.PoolableObjectFactory;

import junit.framework.TestCase;


/**
 * Run N threads concurrents (n = 2...20) for a certain amount of time
 * (20 secs?). Each thread counts its iterations and in each iteration
 * attempts to allocate an array or integers of fixed size
 * (100,500,1000,5000,10000).
 * 
 * Compare the average number of iterations threads made when
 * allocations are performed:
 * 
 * 1) using new/ = null (Garbage collector)<br/>
 * 2) using Jakarta Commons' Pool: StackObjectPool (no max boundary) 
 */
public class ObjectAllocationSpeed extends TestCase {

    /**
     *
     */
    public ObjectAllocationSpeed() {
        super();
    }

    /**
     * @param arg0
     */
    public ObjectAllocationSpeed(String arg0) {
        super(arg0);
    }
    
    private boolean finishLoop = false;
    
    public static class WorkThread extends Thread {
        
        public int iterations = 0;
        public int allocationSize;
        public ObjectAllocationSpeed loopmarker ;
        
        public WorkThread(ObjectAllocationSpeed loopmarker, ThreadGroup tg, String name) {
            super(tg, name);
            this.loopmarker = loopmarker;
        }

        public void setAllocationSize(int size) {
            this.allocationSize = size;
        }
        
        public int getIterations() {
            return this.iterations;
        }
    };
    
    public final static class WorkThreadNewAndGC extends WorkThread
    {
        public WorkThreadNewAndGC(ObjectAllocationSpeed loopmarker, ThreadGroup tg, String name) {
            super( loopmarker, tg, name );
        }
        
        public void run() {
            while (!loopmarker.finishLoop) {
                // allocate.
                int [] memory = new int[ allocationSize ];
                // use.
                memory[0] = memory[1];
                // recycle.
                memory = null;
                iterations++;
            }
        }
    } 
    
    public final static class WorkThreadPool extends WorkThread
        implements PoolableObjectFactory
    {
        private org.apache.commons.pool.impl.StackObjectPool pool;

        public WorkThreadPool(ObjectAllocationSpeed loopmarker, ThreadGroup tg, String name) {
            super( loopmarker, tg, name );
        }
        
        public void setAllocationSize(int size) {
            super.setAllocationSize(size);
            pool = new org.apache.commons.pool.impl.StackObjectPool(this, size);
        }
        

        public void run() {
            while (!loopmarker.finishLoop) {
                // allocate.
                int[] memory;
				try {
					memory = (int[]) pool.borrowObject();
                    // use.
                    memory[0] = memory[1];
                    // recycle.
                    pool.returnObject(memory);
				} catch (Exception e) {
                    throw new RuntimeException(e);
				}
                iterations++;
            }
        }

		/* 
		 */
		public Object makeObject() throws Exception {
			return new int[ this.allocationSize ];
		}

		/* 
		 */
		public void destroyObject(Object obj) throws Exception {
            obj = null;
		}

		/* 
		 */
		public boolean validateObject(Object obj) {
			return true;
		}

		/* 
		 */
		public final void activateObject(Object obj) throws Exception {
		}

		/* 
		 */
		public final void passivateObject(Object obj) throws Exception {
		}
    } 
    
    
    public void testMemoryAllocationSpeed() {
        final int [] THREADS = new int [] {
            2,5,10,20
        };

        final int TIME = 10 * 1000;

        final int [] ARRAY_SIZE = new int [] {
            100, 500, 1000, 5000, 10000
        };
        
        final Class [] CLASSES = {
            WorkThreadNewAndGC.class,
            WorkThreadPool.class
        };

        // the loop :)
        for (int threadsIndex = 0; threadsIndex < THREADS.length; threadsIndex++) {
            int threads = THREADS[threadsIndex];

            for (int arraySizeIndex = 0; arraySizeIndex< ARRAY_SIZE.length; arraySizeIndex++) {
                int arraySize = ARRAY_SIZE[arraySizeIndex];
                
                for (int clazzIndex = 0; clazzIndex < CLASSES.length; clazzIndex++) {
                    Class clazz = CLASSES[clazzIndex];
                    WorkThread [] threadsArray = new WorkThread[ threads ];
                    
                    ThreadGroup tgroup = new ThreadGroup("Workers");

                    // now release all threads
                    this.finishLoop = false;

                    for (int i=0;i<threads;i++) {
                        Object w;
						try {
                            String name = "Thread " + i;
							w = clazz.getConstructor(
                                new Class[]	{ this.getClass(), ThreadGroup.class, String.class })
                                    .newInstance(new Object [] {this, tgroup, name});
						} catch (Exception e) {
                            throw new RuntimeException(e);
						}
                        WorkThread wt = (WorkThread) w;
                        threadsArray[i] = wt;
                        wt.setAllocationSize(arraySize);
                        wt.start();
                    }

                    try {
                        synchronized (this) {
						  this.wait(TIME);
                        }
					} catch (InterruptedException e) {
					}
                    this.finishLoop = true;
                    
                    // counting averages.
                    System.out.print(threads + "," + arraySize
                        + "," + clazz.getName() + ",");

                    for (int i=0;i<threads;i++) {
                        try {
							threadsArray[i].join();
						} catch (InterruptedException e1) {
						}
                        System.out.print(threadsArray[i].getIterations() + ",");
                    }
                    System.out.println();
                }
            } 
        }
        
    }
}
