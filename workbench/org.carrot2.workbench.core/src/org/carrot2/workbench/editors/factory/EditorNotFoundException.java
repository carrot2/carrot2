
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

package org.carrot2.workbench.editors.factory;


/**
 * Thrown by {@link EditorFactory}.
 */
@SuppressWarnings("serial")
public final class EditorNotFoundException extends RuntimeException
{
    public EditorNotFoundException()
    {
        super();
    }

    public EditorNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EditorNotFoundException(String message)
    {
        super(message);
    }

    public EditorNotFoundException(Throwable cause)
    {
        super(cause);
    }
}
