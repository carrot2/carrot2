
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
 * Exception thrown if component initialization was unsuccessful.
 *
 * @see IProcessingComponent#init(IControllerContext)
 */
@SuppressWarnings("serial")
public class ComponentInitializationException extends ProcessingException
{
    public ComponentInitializationException(String message)
    {
        super(message);
    }

    public ComponentInitializationException(Throwable t)
    {
        super(t);
    }

    public ComponentInitializationException(String message, Throwable t)
    {
        super(message, t);
    }
}
