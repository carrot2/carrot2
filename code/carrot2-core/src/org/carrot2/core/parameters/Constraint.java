/**
 * 
 */
package org.carrot2.core.parameters;

/**
 *
 */
public interface Constraint<T>
{
    public <V extends T> boolean isMet(V value);
}
