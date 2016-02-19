
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.simplexml;

import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persist;

/**
 * A wrapper around a type to be serialized by Simple XML. The actual implementation
 * should define the appropriate fields with SimpleXML annotations, possibly also the
 * {@link Persist} and {@link Commit} methods, that will allow full serialization and
 * deserialization of the type.
 */
public interface ISimpleXmlWrapper<T>
{
    /**
     * Returns the value represented by this wrapper.
     */
    T getValue();
    
    /**
     * Sets value to be wrapped by this wrapper.
     */
    void setValue(T value);
}
