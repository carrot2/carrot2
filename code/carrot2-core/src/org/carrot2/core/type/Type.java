/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
public interface Type<T>
{
    public abstract Class<T> getType();

    public abstract T valueOf(String s);
}