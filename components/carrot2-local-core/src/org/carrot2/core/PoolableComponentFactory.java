package org.carrot2.core;

import java.lang.ref.WeakReference;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.PoolableObjectFactory;

/**
 * An adapter between {@link LocalComponentFactory} and {@link PoolableObjectFactory}.
 */
final class PoolableComponentFactory extends BasePoolableObjectFactory
{
    private final LocalComponentFactory factory;
    
    /**
     * We don't keep a strong reference to {@link LocalControllerContext} instance
     * because we could prevent the controller from getting garbage collected.
     */
    private final WeakReference owner;

    public PoolableComponentFactory(LocalComponentFactory factory, LocalControllerContext owner)
    {
        this.factory = factory;
        this.owner = new WeakReference(owner);
    }

    public Object makeObject() throws Exception
    {
        final LocalControllerContext context = (LocalControllerContext) owner.get(); 
        if (context == null) {
            throw new IllegalStateException("Owning context has been GC-ed.");
        }

        final LocalComponent component = factory.getInstance();
        component.init(context);

        return component;
    }
}
