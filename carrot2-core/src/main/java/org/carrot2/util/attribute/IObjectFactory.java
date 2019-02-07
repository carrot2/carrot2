package org.carrot2.util.attribute;

/**
 * Marker interface for types which can exist as assignable {@link Attribute}s.
 */
public interface IObjectFactory<T>
{
    T create();
}
