package org.carrot2.core;

import java.lang.ref.WeakReference;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.commons.pool.PoolableObjectFactory;

/**
 * An adapter between {@link LocalComponentFactory} and {@link PoolableObjectFactory}.
 */
final class PoolableComponentFactory extends BaseKeyedPoolableObjectFactory
{
    /**
     * We don't keep a strong reference to {@link LocalControllerContext} instance
     * because we could prevent the controller from getting garbage collected.
     * 
     * http://issues.carrot2.org/browse/CARROT-145
     */
    private final WeakReference owner;

    /**
     * 
     */
    public PoolableComponentFactory(LocalControllerBase owner)
    {
        this.owner = new WeakReference(owner);
    }

    /**
     * 
     */
    public Object makeObject(Object id) throws Exception
    {
        final LocalControllerBase context = (LocalControllerBase) owner.get(); 
        if (context == null) {
            throw new IllegalStateException("Owning context has been GC-ed.");
        }

        final LocalComponent component = 
            ((LocalComponentFactory) context.componentFactories.get(id)).getInstance();

        component.init(context);

        return component;
    }
}
