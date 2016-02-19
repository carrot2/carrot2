
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

/**
 * An exception thrown if processing failed. For certain specific failure reasons,
 * subclasses of this exception have been defined.
 */
@SuppressWarnings("serial")
public class ProcessingException extends RuntimeException
{
    public ProcessingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ProcessingException(String message)
    {
        super(message);
    }

    public ProcessingException(Throwable cause)
    {
        super(cause);
    }
}
