package com.chilang.util;


/**
 * Interface for general filter that accept or reject object
 */
public interface Filter {
    public boolean accept(Object o);
}
