package com.dawidweiss.carrot.util.common.pools;


/**
 * A limitless objects pool that allocates new objects 
 * as needed and is ready to reuse them when <code>reuse()</code>
 * method is called.
 */
public class DummyReusableObjectsPool implements ReusableObjectsPool {

    private final ReusableObjectsFactory factory;
    private final Object [] ob = new Object [1];

    public DummyReusableObjectsPool(ReusableObjectsFactory factory) {
        this.factory = factory;
    }
    
    public void reuse() {
    }
    
    public Object acquireObject() {
        factory.createNewObjects(ob);
        return ob[0];
    }    

}
