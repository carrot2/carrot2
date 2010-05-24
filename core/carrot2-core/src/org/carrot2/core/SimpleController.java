/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.util.Map;

/**
 * A simple controller implementing the life cycle described in
 * {@link IProcessingComponent}.
 * <p>
 * This controller is useful for one-time processing either with existing component
 * instances or classes of components to be created for processing. In case component
 * classes are used, for <b>each query</b> the controller creates, initializes and
 * destroys instances of all components involved in the processing. For long-running
 * applications (e.g. web applications) please consider the
 * {@link ControllerFactory#createCachingPooling(Class...)}, which offers processing component
 * pooling and result caching.
 * <p>
 * Thread-safety of processing on instantiated component (
 * {@link #process(Map, Object...)}) instances is not enforced in any way
 * and must be assured externally. Processing on component classes (
 * {@link #process(Map, Class...)}) is thread safe, but there is an additional overhead of
 * creating new component instances for each query (which may or may not be a performance
 * issue, this depends on a given component).
 * 
 * @deprecated Please use {@link ControllerFactory#createSimple()} to obtain a
 *             {@link Controller} equivalent to this one. {@link Controller}s can be
 *             further tuned with custom {@link IProcessingComponentManager}s. This class
 *             will be removed in the 3.4.0 release of Carrot2.
 */
public final class SimpleController implements IController
{
    private Controller delegate = new Controller(new SimpleProcessingComponentManager());

    public void init(Map<String, Object> initAttributes)
        throws ComponentInitializationException
    {
        this.delegate.init(initAttributes);
    }

    public void init(Map<String, Object> attributes,
        ProcessingComponentConfiguration... configurations)
        throws ComponentInitializationException
    {
        delegate.init(attributes, configurations);
    }

    public ProcessingResult process(Map<String, Object> attributes,
        Class<?>... processingComponentClasses) throws ProcessingException
    {
        return delegate.process(attributes, processingComponentClasses);
    }

    public ProcessingResult process(Map<String, Object> attributes,
        Object... processingComponentClassesOrIds) throws ProcessingException
    {
        return delegate.process(attributes, processingComponentClassesOrIds);
    }

    public void dispose()
    {
        delegate.dispose();
    }
}
