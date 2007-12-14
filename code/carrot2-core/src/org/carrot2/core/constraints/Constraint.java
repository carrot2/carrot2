/**
 * 
 */
package org.carrot2.core.constraints;

/**
 *
 */
public interface Constraint<T>
{
    public <V extends T> boolean isMet(V value);
}
