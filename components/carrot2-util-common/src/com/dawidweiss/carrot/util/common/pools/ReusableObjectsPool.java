package com.dawidweiss.carrot.util.common.pools;

/**
 */
public interface ReusableObjectsPool {
    public abstract void reuse();
    public abstract Object acquireObject();
}