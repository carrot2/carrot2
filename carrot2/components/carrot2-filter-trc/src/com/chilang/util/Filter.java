/**
 * 
 * @author chilang
 * Created 2003-10-08, 18:26:34.
 */
package com.chilang.util;


/**
 * Interface for general filter that accept or reject object
 */
public interface Filter {
    public boolean accept(Object o);
}
