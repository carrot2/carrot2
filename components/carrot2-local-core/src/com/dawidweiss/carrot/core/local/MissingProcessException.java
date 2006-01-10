
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.core.local;

/**
 * An exception thrown if an operation has been attempted on a process
 * identifier that is not associated with any factory.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
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