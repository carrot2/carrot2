package org.carrot2.core.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * XML persistence layer for a given type.
 */
public interface IXMLPersister<T>
{
    T fromXML(InputStream is) throws IOException;
    void toXML(T value, OutputStream os) throws IOException;
}
