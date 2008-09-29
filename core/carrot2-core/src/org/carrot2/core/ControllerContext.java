package org.carrot2.core;

/**
 * <p>
 * The controller context is a map of key-value pairs, attached to an initialized
 * {@link Controller} instance. The context is created in
 * {@link Controller#init(java.util.Map)} method and remains valid until
 * {@link Controller#dispose()} is invoked.
 * </p>
 * <p>
 * The context instance is passed to all components that take part in query processing
 * inside the controller object ({@link ProcessingComponent#init(ControllerContext)}).
 * {@link ProcessingComponent} implementations may use the context object to store data
 * shared between <i>all</i> component instances (such as thread pools, counters, etc.).
 * In such scenario it is essential to remember to attach a
 * {@link ControllerContextListener} and clean up any resources when the controller is
 * destroyed.
 * </p>
 * 
 * @see Controller#dispose()
 * @see ProcessingComponent#init(ControllerContext)
 */
public interface ControllerContext
{
    /**
     * Atomically binds the given key to the value. Component implementors are encouraged
     * to use custom namespaces to avoid conflicts.
     */
    public void setAttribute(String key, Object value);

    /**
     * Atomically retrieves the value for a given key. Component implementors are
     * encouraged to use custom namespaces to avoid conflicts.
     */
    public Object getAttribute(String key);

    /**
     * Adds a {@link ControllerContextListener} to this context.
     */
    public void addListener(ControllerContextListener listener);

    /**
     * Removes a {@link ControllerContextListener} from this context.
     */
    public void removeListener(ControllerContextListener listener);
}
