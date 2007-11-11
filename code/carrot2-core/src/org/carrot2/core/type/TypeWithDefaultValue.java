/**
 * 
 */
package org.carrot2.core.type;

/**
 *
 */
public interface TypeWithDefaultValue<T> extends Type<T>
{
    public T getDefaultValue();
}
