/*
 * MissingProcessException.java
 * 
 * Created on 2004-06-28
 */
package com.dawidweiss.carrot.core.local;

/**
 * An exception thrown if an operation has been attempted on a process
 * identifier that is not associated with any factory.
 * 
 * @author stachoo
 */
public class MissingProcessException extends Exception
{
    /**
     * @param message
     */
    public MissingProcessException(String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public MissingProcessException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public MissingProcessException(Throwable cause)
    {
        super(cause);
    }
}