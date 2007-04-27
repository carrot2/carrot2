
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs;

/**
 * Configuration exception is thrown from subclasses of {@link AppBase}
 * when the configuration failed for some reason. 
 * 
 * @author Dawid Weiss
 */
public class ConfigurationException extends Exception
{
    public ConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConfigurationException(String message)
    {
        super(message);
    }
}
