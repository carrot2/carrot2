package org.carrot2.util.simplexml;

import org.simpleframework.xml.load.Commit;
import org.simpleframework.xml.load.Persist;

/**
 * A wrapper around a type to be serialized by Simple XML. The actual implementation
 * should define the appropriate fields with SimpleXML annotations, possibly also the
 * {@link Persist} and {@link Commit} methods, that will allow full serialization and
 * deserialization of the type.
 */
public interface SimpleXmlWrapper<T>
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