
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.util.common.pools;


/**
 * A limitless objects pool that allocates new objects 
 * as needed and is ready to reuse them when <code>reuse()</code>
 * method is called.
 */
public class HardReusableObjectsPool implements ReusableObjectsPool {

    private final int minHardLinkSize;
    private final int softLinkIncrementSize;
    private final ReusableObjectsFactory factory;
    
    private final Block firstBlock;
    private Block currentBlock;
    private int   currentElement;
 
    private class Block {
        Block nextBlock;
        Object [] content;
        
        public Block(int size) {
            content = new Object [size];
            factory.createNewObjects(content);
        }

        public Block nextBlock() {
            Block next;

            if (nextBlock != null) {
                next = nextBlock;
            } else {
                next = null;
            }

            if (next == null) {
                next = new Block(softLinkIncrementSize);
                nextBlock = next;
            }
            return next;
        }
    }

    public HardReusableObjectsPool(ReusableObjectsFactory factory, int minHardLinkSize, int softLinkIncrementSize) {
        this.minHardLinkSize = minHardLinkSize;
        this.softLinkIncrementSize = softLinkIncrementSize;
        this.factory = factory;
        this.firstBlock = new Block(minHardLinkSize);
        this.currentBlock = firstBlock;
        this.currentElement = 0;
    }
    
    public void reuse() {
        this.currentBlock = firstBlock;
        this.currentElement = 0;
    }
    
    public Object acquireObject() {
        Object ret = currentBlock.content[currentElement];
        currentElement++;
        if (currentElement >= currentBlock.content.length) {
            // go to the next block.
            currentBlock = currentBlock.nextBlock();
            currentElement = 0;
        }
        return ret;
    }

    /* 
     */
    public void setHardPoolSize(int hardPoolSize) throws NoSuchMethodException {
    }

    /* 
     */
    public void setPoolBlockIncrement(int poolBlockIncrement) throws NoSuchMethodException {
    }

    /* 
     */
    public void setObjectsFactory(ReusableObjectsFactory factory) {
    }    

}